package com.vava.app.model;

/**
 * Datova trieda opisujuca polohu pomocou atributov latitude a longitude.
 * Atributy su pouzivane na konvertovanie na adresu pomocou Google APIs.
 * 
 * @author erikubuntu
 *
 */
public class Location {
	private double latitude;
	private double longitude;
	private final static double AVERAGE_RADIUS_OF_EARTH_KM = 6371;

	public Location(double lat, double lon) {
		latitude = lat;
		longitude = lon;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public static double calculateDistanceInKilometers(double latitude, double longitude, double otherLat, double ohterLng) {

		double latDistance = Math.toRadians(latitude - otherLat);
		double lngDistance = Math.toRadians(longitude - ohterLng);

		double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(latitude))
				* Math.cos(Math.toRadians(otherLat)) * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

		return AVERAGE_RADIUS_OF_EARTH_KM * c;
	}
}
