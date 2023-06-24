package com.starter.issuechecker

public sealed interface CheckResult {

    public data class Success(
        val issueUrl: String,
        val issueStatus: IssueStatus,
    ) : CheckResult

    public data class Error(
        val issueUrl: String,
        val throwable: Throwable,
    ) : CheckResult
}

public enum class IssueStatus {
    Open,
    Closed,
}
