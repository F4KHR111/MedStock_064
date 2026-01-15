package com.example.medstock.view.route

import com.example.medstock.R

object DestinasiEditResep : DestinasiNavigasi {
    override val route = "edit_resep"
    override val titleRes = R.string.edit_resep
    const val resepIdArg = "resepId"
    val routeWithArgs = "$route/{$resepIdArg}"
}