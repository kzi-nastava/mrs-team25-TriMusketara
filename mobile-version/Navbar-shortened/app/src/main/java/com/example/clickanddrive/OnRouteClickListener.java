package com.example.clickanddrive;

import com.example.clickanddrive.dtosample.FavoriteRouteSampleDTO;
import com.example.clickanddrive.dtosample.responses.RouteFromFavoritesResponse;

public interface OnRouteClickListener {
    void onRouteClick(RouteFromFavoritesResponse route);
}
