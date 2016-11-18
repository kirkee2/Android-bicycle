package com.example.lkj.bicycleproject.Structure;

/**
 * Created by leegunjoon on 2016. 11. 18..
 */
public class WeatherStructure {
    private String timeStamp;
    private int hour;
    private int day;
    private double temperature;
    private String weatherState;
    private int rainPercent;
    private int windSpeed;
    private int windDirection;
    private int reh;

    public WeatherStructure() {
    }

    public String getTimeStamp() {

        return timeStamp;
    }

    public int getHour() {
        return hour;
    }

    public int getDay() {
        return day;
    }

    public String getWeatherState() {
        return weatherState;
    }

    public double getTemperature() {
        return temperature;
    }

    public int getRainPercent() {
        return rainPercent;
    }

    public int getWindDirection() {
        return windDirection;
    }

    public int getWindSpeed() {
        return windSpeed;
    }

    public int getReh() {
        return reh;
    }

    public void setHour(int hour) {

        this.hour = hour;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setWeatherState(String weatherState) {
        this.weatherState = weatherState;
    }

    public void setRainPercent(int rainPercent) {
        this.rainPercent = rainPercent;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public void setWindSpeed(int windSpeed) {
        this.windSpeed = windSpeed;
    }

    public void setWindDirection(int windDirection) {
        this.windDirection = windDirection;
    }

    public void setReh(int reh) {
        this.reh = reh;
    }
}
