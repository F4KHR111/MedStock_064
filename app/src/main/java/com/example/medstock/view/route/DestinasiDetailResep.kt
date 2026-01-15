package com.example.medstock.view.route

import com.example.medstock.R

object DestinasiDetailResep : DestinasiNavigasi {
    override val route = "detail_resep"
    override val titleRes = R.string.detail_resep
    const val resepIdArg = "resepId"
    val routeWithArgs = "$route/{$resepIdArg}"
}