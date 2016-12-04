package com.xtel.vparking.model.entity;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Lê Công Long Vũ on 9/7/2016.
 */
public class MapModel {
    @SerializedName("routes")
    private ArrayList<Routers> routers;

    public ArrayList<Routers> getRouters() {
        return routers;
    }

    public void setRouters(ArrayList<Routers> routers) {
        this.routers = routers;
    }

    public class Routers {
        @SerializedName("legs")
        private ArrayList<Legs> legses;

        public ArrayList<Legs> getLegses() {
            return legses;
        }

        public void setLegses(ArrayList<Legs> legses) {
            this.legses = legses;
        }

        public class Legs {
            @SerializedName("steps")
            private ArrayList<Steps> stepses;

            public ArrayList<Steps> getStepses() {
                return stepses;
            }

            public void setStepses(ArrayList<Steps> stepses) {
                this.stepses = stepses;
            }

            public class Steps {
                @SerializedName("start_location")
                private StartLocation startLocation;
                @SerializedName("end_location")
                private EndLocation endLocation;
                @SerializedName("polyline")
                private Polyline polyline;

                public StartLocation getStartLocation() {
                    return startLocation;
                }

                public void setStartLocation(StartLocation startLocation) {
                    this.startLocation = startLocation;
                }

                public EndLocation getEndLocation() {
                    return endLocation;
                }

                public void setEndLocation(EndLocation endLocation) {
                    this.endLocation = endLocation;
                }

                public Polyline getPolyline() {
                    return polyline;
                }

                public void setPolyline(Polyline polyline) {
                    this.polyline = polyline;
                }

                public class StartLocation {
                    @SerializedName("lat")
                    private Double lat;
                    @SerializedName("lng")
                    private Double lng;

                    public Double getLat() {
                        return lat;
                    }

                    public void setLat(Double lat) {
                        this.lat = lat;
                    }

                    public Double getLng() {
                        return lng;
                    }

                    public void setLng(Double lng) {
                        this.lng = lng;
                    }
                }

                public class EndLocation {
                    @SerializedName("lat")
                    private Double lat;
                    @SerializedName("lng")
                    private Double lng;

                    public Double getLat() {
                        return lat;
                    }

                    public void setLat(Double lat) {
                        this.lat = lat;
                    }

                    public Double getLng() {
                        return lng;
                    }

                    public void setLng(Double lng) {
                        this.lng = lng;
                    }
                }

                public class Polyline {
                    @SerializedName("points")
                    private String Points;

                    public String getPoints() {
                        return Points;
                    }

                    public void setPoints(String points) {
                        Points = points;
                    }
                }
            }
        }
    }
}
