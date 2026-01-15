package com.example.medstock.view.route

import com.example.medstock.R

object DestinasiEditObat : DestinasiNavigasi {
    override val route = "edit_obat"
    override val titleRes = R.string.edit_obat
    const val obatIdArg = "obatId"
    val routeWithArgs = "$route/{$obatIdArg}"
}