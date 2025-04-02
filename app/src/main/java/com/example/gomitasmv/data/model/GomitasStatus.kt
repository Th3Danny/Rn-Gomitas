package com.example.gomitasmv.data.model



 // Enumeración para los posibles estados de un botón

enum class GomitasStatus {
    NORMAL,
    DEFECTIVE,
    UNKNOWN;

    companion object {
        fun fromString(status: String?): GomitasStatus {
            return when (status?.lowercase()) {
                "sano", "normal" -> NORMAL
                "enfermo", "defectuoso", "defecto" -> DEFECTIVE
                null -> UNKNOWN
                else -> UNKNOWN
            }
        }
    }

}