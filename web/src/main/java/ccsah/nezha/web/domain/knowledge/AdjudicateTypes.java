package ccsah.nezha.web.domain.knowledge;

/**
 * Created by rexxar on 19-11-5.
 */
public enum AdjudicateTypes {
    NONE,//
    PENDING,//待审核
    REJECT,//已拒绝待返工
    APPROVE,//已归档
    DELETED,//仅用于查询类别不用做存储
    DRAFT//已更新但未提交待审的草稿
}
