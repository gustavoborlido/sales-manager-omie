package com.omie.salesmanager.enum

enum class SalesScreenEnum(val route: String) {
    SalesAuthView("SalesAuthView"),
    SalesOrderListView("SalesOrderListView"),
    SalesOrderAddView("SalesOrderAddView"),
    SalesItemListView("SalesItemListView/{orderId}"),
    SalesItemAddView("SalesItemAddView/{orderId}");

    companion object {
        fun getRoute(screen: SalesScreenEnum, params: Map<String, String>? = null): String {
            var route = screen.route
            params?.forEach { (key, value) ->
                route = route.replace("{$key}", value)
            }
            return route
        }
    }
}
