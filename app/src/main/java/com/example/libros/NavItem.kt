package com.example.libros

sealed class NavItem(var route: String, var icon: Int, var title: String)
{
    object Insertar : NavItem("home", R.drawable.ic_add, "Insertar")
    object Borrar : NavItem("profile", R.drawable.ic_delete, "Borrar")
    object Visualizar : NavItem("settings", R.drawable.ic_view, "Visualizar")

}
