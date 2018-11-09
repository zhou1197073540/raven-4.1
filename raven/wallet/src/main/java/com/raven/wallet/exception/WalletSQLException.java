package com.raven.wallet.exception;

import com.raven.wallet.dto.TaskDto;

public class WalletSQLException extends RuntimeException {

    private static final long serialVersionUID = -2996489172797289506L;

    private TaskDto dto;

    public WalletSQLException() {
        super();
    }

    public WalletSQLException(String message) {
        super(message);
    }

    public WalletSQLException(String message, Throwable cause) {
        super(message, cause);
    }

    public WalletSQLException(TaskDto dto, String message, Throwable cause) {
        super(message, cause);
        this.dto = dto;
    }

    public WalletSQLException(Throwable cause) {
        super(cause);
    }

    protected WalletSQLException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public TaskDto getDto() {
        return dto;
    }

    public void setDto(TaskDto dto) {
        this.dto = dto;
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return super.fillInStackTrace();
    }
}
