/*
 * Copyright (c) 2017 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration. All Rights Reserved.
 */

package gov.nasa.worldwind.ogc;

import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.render.ImageSource;
import gov.nasa.worldwind.render.ImageTile;
import gov.nasa.worldwind.util.Level;
import gov.nasa.worldwind.util.Logger;
import gov.nasa.worldwind.util.Tile;
import gov.nasa.worldwind.util.TileFactory;

/**
 * Factory for constructing WCS version 1.0.0 URLs associated with WCS Get Coverage requests.
 */
public class Wcs100TileFactory implements TileFactory {

    /**
     * The WCS service address use to build Get Coverage URLs.
     */
    protected String serviceAddress;

    /**
     * The coverage name of the desired WCS data.
     */
    protected String coverage;

    /**
     * Constructs a WCS Get Coverage URL builder with the specified WCS service address and coverage. The generated URL
     * will be pursuant to version 1.0.0 WCS specification and use image/tiff as the format and EPSG:4326 as the
     * coordinate reference system.
     *
     * @param serviceAddress the WCS service address
     * @param coverage       the WCS coverage name
     *
     * @throws IllegalArgumentException If any of the parameters are null
     */
    public Wcs100TileFactory(String serviceAddress, String coverage) {
        if (serviceAddress == null) {
            throw new IllegalArgumentException(
                Logger.makeMessage("Wcs100TileFactory", "constructor", "missingServiceAddress"));
        }

        if (coverage == null) {
            throw new IllegalArgumentException(
                Logger.makeMessage("Wcs100TileFactory", "constructor", "The coverage is null"));
        }

        this.serviceAddress = serviceAddress;
        this.coverage = coverage;
    }

    /**
     * Indicates the WCS service address used to build Get Coverage URLs.
     *
     * @return the WCS service address
     */
    public String getServiceAddress() {
        return this.serviceAddress;
    }

    /**
     * Sets the WCS service address used to build Get Coverage URLs.
     *
     * @param serviceAddress the WCS service address
     *
     * @throws IllegalArgumentException If the service address is null
     */
    public void setServiceAddress(String serviceAddress) {
        if (serviceAddress == null) {
            throw new IllegalArgumentException(
                Logger.logMessage(Logger.ERROR, "Wcs100TileFactory", "setServiceAddress", "missingServiceAddress"));
        }

        this.serviceAddress = serviceAddress;
    }

    /**
     * Indicates the coverage name used to build Get Coverage URLs.
     *
     * @return the coverage name
     */
    public String getCoverage() {
        return this.coverage;
    }

    /**
     * Sets the coverage name used to build Get Coverage URLs.
     *
     * @param coverage the coverage name
     *
     * @throws IllegalArgumentException If the coverage name is null
     */
    public void setCoverage(String coverage) {
        if (coverage == null) {
            throw new IllegalArgumentException(
                Logger.makeMessage("Wcs100TileFactory", "constructor", "The coverage is null"));
        }

        this.coverage = coverage;
    }

    @Override
    public Tile createTile(Sector sector, Level level, int row, int column) {
        ImageTile tile = new ImageTile(sector, level, row, column);

        String urlString = this.urlForTile(sector, level);
        if (urlString != null) {
            tile.setImageSource(ImageSource.fromUrl(urlString));
        }

        return tile;
    }

    protected String urlForTile(Sector sector, Level level) {
        StringBuilder url = new StringBuilder(this.serviceAddress);

        int index = url.indexOf("?");
        if (index < 0) { // if service address contains no query delimiter
            url.append("?"); // add one
        } else if (index != url.length() - 1) { // else if query delimiter not at end of string
            index = url.lastIndexOf("&");
            if (index != url.length() - 1) {
                url.append("&"); // add a parameter delimiter
            }
        }

        url.append("SERVICE=WCS&VERSION=1.0.0&REQUEST=GetCoverage&COVERAGE=").append(this.coverage).append("&");
        url.append("CRS=EPSG:4326&FORMAT=image/tiff&");
        url.append("WIDTH=").append(level.tileWidth).append("&");
        url.append("HEIGHT=").append(level.tileHeight).append("&");
        url.append("BBOX=").append(sector.minLongitude()).append(",").append(sector.minLatitude()).append(",");
        url.append(sector.maxLongitude()).append(",").append(sector.maxLatitude());

        return url.toString();
    }
}
