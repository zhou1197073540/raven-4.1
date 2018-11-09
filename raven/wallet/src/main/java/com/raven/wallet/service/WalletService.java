package com.raven.wallet.service;


import com.raven.wallet.bean.WalletUserFrozenBean;
import com.raven.wallet.consts.Const;
import com.raven.wallet.consts.COMMENT;
import com.raven.wallet.dto.*;
import com.raven.wallet.exception.WalletSQLException;
import com.raven.wallet.mapper.WalletMapper;
import com.raven.wallet.utils.HashUtil;
import com.raven.wallet.utils.ObjectUtil;
import com.raven.wallet.utils.RedisUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class WalletService {

    private static int ONE_MIN = 60;
    private static int ONE_HR = 3600;
    private static int FIVE_HR = 3600 * 5;
    private static final Logger log = LoggerFactory.getLogger(WalletService.class);

    @Autowired
    WalletMapper walletMapper;

    @Autowired
    RestTemplate restTemplate;

    public RespDto getMoney(TaskDto dto) throws Exception {
        String value = getUserMoneyByUidAndType(dto);
        if (!StringUtils.isEmpty(value)) {
            return new RespDto(Const.SUCCESS, Const.SUCCESS_MSG, value);

        }
        return new RespDto(Const.NOT_FOUND, Const.NOT_FOUND_MSG, null);
    }

    public RespDto getMoneyByDate(QueryDto dto) throws Exception {
        String value = getUserMoneyByUidAndTypeAndDate(dto);
        if (!StringUtils.isEmpty(value)) {
            return new RespDto(Const.SUCCESS, Const.SUCCESS_MSG, value);
        }
        return new RespDto(Const.NOT_FOUND, Const.NOT_FOUND_MSG, null);
    }


    public RespDto getMoneyList(TaskDto dto) throws Exception {
        List<Map<String, Object>> points = getUserMoneyListByUidAndType(dto);
        if (points.isEmpty()) {
            return new RespDto(Const.NOT_FOUND, Const.NOT_FOUND_MSG, null);
        }
        return new RespDto(Const.SUCCESS, Const.SUCCESS_MSG, points);
    }

    public List<Map<String, Object>> getUserMoneyListByUidAndType(TaskDto dto) throws Exception {
        return walletMapper.getUserMoneyListByUidAndType(dto);
    }

    public String getUserMoneyByUidAndType(TaskDto taskDto) throws Exception {
        Jedis r_conn = RedisUtil.getJedis();
        String uPointKey = taskDto.getUid() + taskDto.getTypeStr();
        String value = r_conn.get(uPointKey);
        try {
            if (value != null) {
                return value;
            } else {
                Integer remaining = walletMapper.getUserMoneyByUidAndType(taskDto);
                return (remaining != null) ? remaining.toString() : "";
            }
        } finally {
            r_conn.close();
        }
    }

    public String getUserMoneyByUidAndTypeAndDate(QueryDto queryDto) throws Exception {
        Integer remaining = walletMapper.getUserMoneyByUidAndTypeAndDateRange(queryDto);
        return (remaining != null) ? remaining.toString() : "";
    }

    /**
     * 更改钱包活动余额
     *
     * @param dto
     * @throws Exception
     */
    public void changeWallet(TaskDto dto) throws Exception {

        //todo check serial_num exist??
        Jedis r_conn = RedisUtil.getJedis();
        String uKey = dto.getUid() + dto.getTypeStr();
        String value = r_conn.get(uKey);
        try {
            if (value != null) {
                //续期
                long expire_t = r_conn.ttl(uKey);
                if (expire_t - Instant.now().getEpochSecond() < ONE_HR) {
                    r_conn.expireAt(uKey, Instant.now().getEpochSecond() + FIVE_HR);
                }

                //更新,异常补款
                long value_l = r_conn.incrBy(uKey, dto.getChange());
                if (value_l < 0) {
                    r_conn.incrBy(uKey, Math.abs(dto.getChange()));
                    callback(Const.NOT_ENOUGH, Const.NOT_ENOUGH_MSG, dto);
                } else {
                    try {
                        taggingDto(dto);
                        walletMapper.addWalletUserChangeLog(dto);
                    } catch (Exception e) {
                        r_conn.incrBy(uKey, -dto.getChange());
                        throw new WalletSQLException(dto, dto.toString(), e);
                    }
                    callback(Const.SUCCESS, Const.SUCCESS_MSG, dto);
                }
            } else {
                updateRemainingWithRedisLock(uKey, dto);
                r_conn.close();
                changeWallet(dto);
            }
        } finally {
            closeJedis(r_conn);
        }
    }

    /**
     * 冻结钱包部分money
     *
     * @param dto
     * @throws Exception
     */
    public void freezeWallet(TaskDto dto) throws Exception {
        Jedis r_conn = RedisUtil.getJedis();
        String uKey = dto.getUid() + dto.getTypeStr();
        String value = r_conn.get(uKey);
        try {
            if (value != null) {
                long value_l = r_conn.incrBy(uKey, -dto.getChange());
                if (value_l < 0) {
                    r_conn.incrBy(uKey, Math.abs(dto.getChange()));
                    callback(Const.NOT_ENOUGH, Const.NOT_ENOUGH_MSG, dto);
                } else {
                    try {
                        taggingDto(dto);
                        walletMapper.addWalletUserFrozen(dto);
                    } catch (Exception e) {
                        r_conn.incrBy(uKey, dto.getChange());
                        throw new WalletSQLException(dto, dto.toString(), e);
                    }
                }
            } else {
                updateRemainingWithRedisLock(uKey, dto);
                r_conn.close();
                freezeWallet(dto);
            }
        } finally {
            closeJedis(r_conn);
        }
    }

    /**
     * 解冻，只支持一一对应
     *
     * @throws Exception
     */
    public void unfreezeWallet(TaskDto dto, boolean isFirst) throws Exception {

        //第一次
        if (isFirst) {
            //不要覆盖dto
            WalletUserFrozenBean bean =
                    walletMapper.getWalletUserFrozenByOidAndFrozenStatus(dto.getOid(), Const.FROZEN);
            if (bean == null) {
                callback(Const.NOT_FOUND, Const.NOT_FOUND_MSG, dto);
                return;
            }
            dto.setChange(bean.getChange());
        }
        Jedis r_conn = RedisUtil.getJedis();
        String uKey = dto.getUid() + dto.getTypeStr();
        String value = r_conn.get(uKey);
        try {
            if (value != null) {
                r_conn.incrBy(uKey, dto.getChange());
                try {
                    int ret = walletMapper.updateWalletUserFrozenStatusByOid(dto.getOid(), Const.UNFROZEN);
                    if (ret > 1) log.info("oid: {} unfreeze {} messages", dto.getOid(), ret);
                } catch (Exception e) {
                    r_conn.incrBy(uKey, -dto.getChange());
                    throw new WalletSQLException(dto, dto.toString(), e);
                }
                callback(Const.SUCCESS, Const.SUCCESS_MSG, dto);
            } else {
                updateRemainingWithRedisLock(uKey, dto);
                r_conn.close();
                unfreezeWallet(dto, false);
            }
        } finally {
            closeJedis(r_conn);
        }
    }


    /**
     * 打标签：
     * 流水号serial_num
     * 创建时间create_time
     * 创建日期create_date
     *
     * @param dto
     */

    private void taggingDto(TaskDto dto) {
        String serialNum = HashUtil.encodeByMD5(dto.getType() +
                dto.getType() + dto.getOid() + dto.getUid() + dto.getTime());
        dto.setSerialNum(serialNum);
        dto.setCreateDate(LocalDate.now().toString());
        dto.setCreateTime(LocalDateTime.now().toString());
    }

    /**
     * 将余额存入redis，锁与用户绑定（一人一锁）
     *
     * @param uKey
     * @param dto
     * @throws Exception
     */
    private void updateRemainingWithRedisLock(String uKey, TaskDto dto) throws Exception {
        Jedis r_conn = RedisUtil.getJedis();
        Integer walletRemaining = 0;
        Integer frozenRemaining = 0;
        Integer remaining = 0;
        long now = Instant.now().getEpochSecond();
        try {
            if (tryRedisLock(uKey)) {
                try {
                    walletRemaining = walletMapper.getUserMoneyByUidAndType(dto);
                    frozenRemaining = walletMapper.getUserFrozenMoneyByUidAndType(dto);
                    walletRemaining = (walletRemaining != null) ? walletRemaining : 0;
                    frozenRemaining = (frozenRemaining != null) ? frozenRemaining : 0;
                    remaining = walletRemaining - frozenRemaining;
                } finally {
                    //防止过期影响
                    if (now - Instant.now().getEpochSecond() < ONE_MIN) {
                        r_conn.set(uKey, remaining.toString());
                        r_conn.expireAt(uKey, Instant.now().getEpochSecond() + FIVE_HR);
                        redisUnlock(uKey);
                    } else {
                        throw new WalletSQLException(dto, dto.toString(), new Throwable("操作超时"));
                    }
                }
            } else {
                Thread.sleep(2000);
            }
        } finally {
            closeJedis(r_conn);
        }
    }

    private void closeJedis(Jedis r_conn) throws Exception {
        try {
            r_conn.close();
        } catch (JedisException e) {
            if (e.getMessage().contains("Could not return the resource to the pool")) {
                //ignore
            } else {
                throw e;
            }
        }
    }

    private void callback(int status, String msg, TaskDto dto) {
        if (StringUtils.isEmpty(dto.getCallbackUrl())) return;
        try {
            restTemplate.put(dto.getCallbackUrl(), new RespDto(status, msg, dto));
        } catch (RestClientException e) {
            e.printStackTrace();
            //todo callback later
        }
    }

    //redis登记表
    private boolean tryRedisLock(String uid) {
        String uLock = uid + ".lock";
        Jedis r_conn = RedisUtil.getJedis();

        //上锁
        try {
            if (r_conn.setnx(uLock, String.valueOf(Instant.now().getEpochSecond())) == 1) {
                return true;
            } else {
                String timestamp = r_conn.get(uLock);
                //超时
                if (Instant.now().getEpochSecond() - Long.parseLong(timestamp) > ONE_MIN) {
                    //重置锁
                    if (timestamp.equals(r_conn.getSet(uLock, String.valueOf(Instant.now().getEpochSecond())))) {
                        return true;
                    }
                }
            }
        } finally {
            r_conn.close();
        }
        return false;
    }

    private void redisUnlock(String uid) {
        String uLock = uid + ".lock";
        Jedis r_conn = RedisUtil.getJedis();
        try {
            r_conn.del(uLock);
        } finally {
            r_conn.close();
        }
    }

    @Transactional
    public void assetsTransfer(TaskDto dto) throws Exception {
        dto.setDtoType(Const.ASSETS, Const.ASSETS_STR);
        PointsDto pointsDto = new PointsDto();
        pointsDto.setChange(-dto.getChange());
        pointsDto.setOid(dto.getOid());
        pointsDto.setUid(dto.getUid());
        pointsDto.setTime(dto.getTime());
        pointsDto.setDate(dto.getDate());
        if (dto.getChange() > 0) {
            dto.setComment(COMMENT.ASSETS_FROM_POINTS.name);
            pointsDto.setComment(COMMENT.POINTS_TO_ASSETS.name);
        } else if (dto.getChange() <= 0) {
            dto.setComment(COMMENT.ASSETS_TO_POINTS.name);
            pointsDto.setComment(COMMENT.POINTS_FROM_ASSETS.name);
        }
        changeWallet(pointsDto);
        changeWallet(dto);
    }

    /**
     * 检查当前积分是否够扣款
     *
     * @param dto
     * @return
     * @throws Exception
     */
    public boolean checkEnoughMoneyForChange(TaskDto dto) throws Exception {
        if (dto.getChange() >= 0) return true;
        String money = getUserMoneyByUidAndType(dto);
        if (StringUtils.isEmpty(money)) {
            return false;
        }
        if (Integer.parseInt(money) >= -dto.getChange()) {
            return true;
        }
        return false;
    }

    /**
     * 检查当前积分是否够转钱
     *
     * @param dto
     * @return
     * @throws Exception
     */
    public boolean checkEnoughPointsForTransfer(TaskDto dto) throws Exception {
        dto.setDtoType(Const.POINTS, Const.POINTS_STR);
        String points = getUserMoneyByUidAndType(dto);
        if (StringUtils.isEmpty(points)) {
            return false;
        }
        if (Integer.parseInt(points) >= Math.abs(dto.getChange())) {
            return true;
        }
        return false;
    }

    /**
     * 检查当前积分是否够转钱
     *
     * @param dto
     * @return
     * @throws Exception
     */
    public boolean checkEnoughAssetsForTransfer(TaskDto dto) throws Exception {
        dto.setDtoType(Const.ASSETS, Const.ASSETS_STR);
        String assets = getUserMoneyByUidAndType(dto);
        if (StringUtils.isEmpty(assets)) {
            return false;
        }
        if (Integer.parseInt(assets) >= Math.abs(dto.getChange())) {
            return true;
        }
        return false;
    }

    @Transactional
    public void changeWalletWithTransaction(TaskDto dto) throws Exception {
        changeWallet(dto);
    }

    public Thread createChangeWalletThread(TaskDto dto) {
        Thread t = new Thread(() -> {
            try {
                changeWalletWithTransaction(dto);
            } catch (WalletSQLException wse) {
                log.error("walletError", wse);
                if (!StringUtils.isBlank(wse.getDto().getCallbackUrl())) {
                    callback(Const.WALLET_ERROR, Const.WALLET_ERROR_MSG, wse.getDto());
                }
            } catch (Exception e) {
                log.error("unhandledError", e);
            }

        });
        t.setUncaughtExceptionHandler((thread, error) ->
                log.error("thread Error", thread, error)
        );
        return t;
    }

    public Thread createAssetsTransferThread(TaskDto dto) {
        Thread t = new Thread(() -> {
            try {
                assetsTransfer(dto);
            } catch (WalletSQLException wse) {
                log.error("walletError", wse);
                if (!StringUtils.isBlank(wse.getDto().getCallbackUrl())) {
                    callback(Const.WALLET_ERROR, Const.WALLET_ERROR_MSG, wse.getDto());
                }
            } catch (Exception e) {
                log.error("unhandledError", e);
            }
        });
        t.setUncaughtExceptionHandler((thread, error) ->
                log.error("thread Error", thread, error)
        );
        return t;
    }

    public Thread createFreezeWalletThread(TaskDto dto) {
        Thread t = new Thread(() -> {
            try {
                freezeWallet(dto);
            } catch (WalletSQLException wse) {
                log.error("walletError", wse);
                if (!StringUtils.isBlank(wse.getDto().getCallbackUrl())) {
                    callback(Const.WALLET_ERROR, Const.WALLET_ERROR_MSG, wse.getDto());
                }
            } catch (Exception e) {
                log.error("unhandledError", e);
            }
        });
        t.setUncaughtExceptionHandler((thread, error) ->
                log.error("thread Error", thread, error)
        );
        return t;
    }

    public Thread createUnfreezeWalletThread(TaskDto dto) {
        Thread t = new Thread(() -> {
            try {
                unfreezeWallet(dto, true);
            } catch (WalletSQLException wse) {
                log.error("walletError", wse);
                if (!StringUtils.isBlank(wse.getDto().getCallbackUrl())) {
                    callback(Const.WALLET_ERROR, Const.WALLET_ERROR_MSG, wse.getDto());
                }
            } catch (Exception e) {
                log.error("unhandledError", e);
            }
        });
        t.setUncaughtExceptionHandler((thread, error) ->
                log.error("thread Error", thread, error)
        );
        return t;
    }

    public Thread createTakerBalanceThread(TaskDto dto) throws Exception {
        TaskDto dto1 = ObjectUtil.clone(dto);
        Thread t = new Thread(() -> {
            try {
                dto.setCallbackUrl("");
                unfreezeWallet(dto, true);
                changeWallet(dto1);
            } catch (WalletSQLException wse) {
                log.error("walletError", wse);
                if (!StringUtils.isBlank(wse.getDto().getCallbackUrl())) {
                    callback(Const.WALLET_ERROR, Const.WALLET_ERROR_MSG, wse.getDto());
                }
            } catch (Exception e) {
                log.error("unhandledError", e);
            }
        });
        t.setUncaughtExceptionHandler((thread, error) ->
                log.error("thread Error", thread, error)
        );
        return t;
    }

}
