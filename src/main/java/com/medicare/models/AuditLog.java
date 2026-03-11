package com.medicare.models;

import java.time.LocalDateTime;

public class AuditLog {

    private int           logId;
    private int           userId;
    private String        action;
    private LocalDateTime timestamp;
    private String        ipAddress;

    // Transient display field (populated by JOIN query)
    private String username;

    public AuditLog() { }

    public AuditLog(int logId, int userId, String action,
                    LocalDateTime timestamp, String ipAddress) {
        this.logId     = logId;
        this.userId    = userId;
        this.action    = action;
        this.timestamp = timestamp;
        this.ipAddress = ipAddress;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public int           getLogId()     { return logId;     }
    public int           getUserId()    { return userId;    }
    public String        getAction()    { return action;    }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String        getIpAddress() { return ipAddress; }
    public String        getUsername()  { return username;  }

    // ── Setters ──────────────────────────────────────────────────────────────

    public void setLogId(int logId)                    { this.logId     = logId;     }
    public void setUserId(int userId)                  { this.userId    = userId;    }
    public void setAction(String action)               { this.action    = action;    }
    public void setTimestamp(LocalDateTime timestamp)  { this.timestamp = timestamp; }
    public void setIpAddress(String ipAddress)         { this.ipAddress = ipAddress; }
    public void setUsername(String username)           { this.username  = username;  }

    @Override
    public String toString() {
        return "AuditLog{logId=" + logId + ", userId=" + userId
               + ", action='" + action + "', timestamp=" + timestamp + "}";
    }
}
