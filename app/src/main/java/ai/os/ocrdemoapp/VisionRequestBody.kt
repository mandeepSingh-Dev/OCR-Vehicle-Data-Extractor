package ai.os.ocrdemoapp


data class VisionRequestBody(
    val requests: List<VisionRequest?>?
) {
    data class VisionRequest(
        val features: List<VisionFeature?>?,
        val image: VisionImage?
    ) {
        data class VisionFeature(
            val type: String?
        )

        data class VisionImage(
            val content: String?
        )
    }
}