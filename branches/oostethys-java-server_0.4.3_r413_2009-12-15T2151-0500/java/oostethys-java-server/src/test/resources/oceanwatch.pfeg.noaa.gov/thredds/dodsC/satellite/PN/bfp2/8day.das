Attributes {
    altitude {
        Float32 actual_range 0.0, 0.0;
        Int32 fraction_digits 0;
        String long_name "Altitude";
        String standard_name "altitude";
        String units "m";
        String axis "Z";
    }
    lat {
        Float32 actual_range -75.0, 75.0;
        String axis "Y";
        String coordsys "geographic";
        Int32 fraction_digits 3;
        String long_name "Latitude";
        String point_spacing "even";
        String standard_name "latitude";
        String units "degrees_north";
    }
    lon {
        Float32 actual_range 0.0, 360.0;
        String axis "X";
        String coordsys "geographic";
        Int32 fraction_digits 3;
        String long_name "Longitude";
        String point_spacing "even";
        String standard_name "longitude";
        String units "degrees_east";
    }
    time {
        Float64 actual_range 1.169856E9, 1.169856E9;
        Int32 fraction_digits 0;
        String long_name "Centered Time";
        String standard_name "time";
        String units "seconds since 1970-01-01T00:00:00Z";
        String axis "T";
    }
    PNbfp2 {
        Float32 _FillValue -9999999.0;
        Float32 actual_range 165.887, 6967.32;
        String coordsys "geographic";
        Int32 fraction_digits 0;
        String long_name "Primary Productivity, NASA Aqua MODIS and GOES Imager, 0.1 degrees, Global, EXPERIMENTAL";
        Float32 missing_value -9999999.0;
        Int32 numberOfObservations 32;
        Float64 percentCoverage 5.9203334035756224E-6;
        String standard_name "net_primary_productivity";
        String units "mg C m-2 day-1";
    }
    NC_GLOBAL {
        String acknowledgement "NOAA NESDIS";
        String cdm_data_type "Grid";
        Int32 cols 3601;
        String composite "true";
        String contributor_name "NASA GSFC (G. Feldman), NOAA NESDIS, CoastWatch West Coast";
        String contributor_role "Source of level 2 data.";
        String Conventions "COARDS, CF-1.0, Unidata Dataset Discovery v1.0, CWHDF";
        String creator_email "dave.foley@noaa.gov";
        String creator_name "NOAA NESDIS CoastWatch WCRN";
        String creator_url "http://coastwatch.pfel.noaa.gov";
        String cwhdf_version "3.4";
        String date_created "2007-02-01Z";
        String date_issued "2007-02-01Z";
        Float32 Easternmost_Easting 360.0;
        Float64 et_affine 0.0, 0.1, 0.1, 0.0, 0.0, -75.0;
        Int32 gctp_datum 12;
        Float64 gctp_parm 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0;
        Int32 gctp_sys 0;
        Int32 gctp_zone 0;
        Float32 geospatial_lat_max 75.0;
        Float32 geospatial_lat_min -75.0;
        Float32 geospatial_lat_resolution 0.1;
        String geospatial_lat_units "degrees_north";
        Float32 geospatial_lon_max 360.0;
        Float32 geospatial_lon_min 0.0;
        Float32 geospatial_lon_resolution 0.1;
        String geospatial_lon_units "degrees_east";
        Float32 geospatial_vertical_max 0.0;
        Float32 geospatial_vertical_min 0.0;
        String geospatial_vertical_positive "up";
        String geospatial_vertical_units "m";
        String history "NASA GSFC (G. Feldman), NOAA NESDIS, CoastWatch West Coast
2007-02-01T21:34:25Z NOAA NESDIS CoastWatch WCRN";
        String id "LPNbfp2S8day_20070127000000";
        String institution "NASA GSFC (G. Feldman), NOAA NESDIS, CoastWatch West Coast";
        String keywords "EARTH SCIENCE > Oceans > Ocean Chemistry > Nutrients";
        String keywords_vocabulary "GCMD Science Keywords";
        String license "The data may be used and redistributed for free but is not intended for legal use, since it may contain inaccuracies. Neither the data Contributor, CoastWatch, NOAA, nor the United States Government, nor any of their employees or contractors, makes any warranty, express or implied, including warranties of merchantability and fitness for a particular purpose, or assumes any legal liability for the accuracy, completeness, or usefulness, of this information.";
        String naming_authority "gov.noaa.pfel.coastwatch";
        Float32 Northernmost_Northing 75.0;
        String origin "NASA GSFC (G. Feldman), NOAA NESDIS, CoastWatch West Coast";
        Int32 pass_date 13536, 13537, 13538, 13539, 13540, 13541, 13542, 13543;
        Float64 polygon_latitude -75.0, 75.0, 75.0, -75.0, -75.0;
        Float64 polygon_longitude 0.0, 0.0, 360.0, 360.0, 0.0;
        String processing_level "3";
        String project "CoastWatch (http://coastwatch.noaa.gov/)";
        String projection "geographic";
        String projection_type "mapped";
        String references "NOAA GOES satellites: http://coastwatch.noaa.gov/goes_sst_overview.html . NOAA GOES satellites: http://www.oso.noaa.gov/goes/index.htm . Processing reference: Wu, X., W. P. Menzel, and G. S. Wade (1999). Estimation of sea surface temperatures using GOES-8/9 radiance measurements. Bull. Amer. Meteor. Soc., 80, 1127-1138. Processing reference: Maturi, E., C. Merchant, A. Harris, X. Li, and B. Potash.  Geostationary Sea Surface Temperature Product Validation and Methodology.  Poster Presentation at the American Meteorological Society's 13th Conference on Satellite Meteorology and Oceanography (P5.16).  Norfolk, VA; 19-23 Sept., 2004. http://ams.confex.com/ams/pdfpapers/79202.pdf .  Aqua/MODIS information: http://oceancolor.gsfc.nasa.gov/ . MODIS information: http://coastwatch.noaa.gov/modis_ocolor_overview.html . Processing reference: Behrenfeld, M. J., and P. G. Falkowski (1997). Photosynthetic Rates Derived from Satellite-Based Chlorophyll Concentration. Limnology and Oceanography.";
        Int32 rows 1501;
        String satellite "Aqua and GOES";
        String sensor "MODIS and Imager";
        String source "satellite observation: Aqua and GOES, MODIS and Imager";
        Float32 Southernmost_Northing -75.0;
        String standard_name_vocabulary "CF-1.0";
        Float64 start_time 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0;
        String summary "Primary Productivity is calculated from NASA Aqua MODIS Chl a and NOAA GOES Imager SST data.  THIS IS AN EXPERIMENTAL PRODUCT: intended strictly for scientific evaluation by professional marine scientists.";
        String time_coverage_end "2007-01-31T00:00:00Z";
        String time_coverage_start "2007-01-23T00:00:00Z";
        String title "Primary Productivity, NASA Aqua MODIS and GOES Imager, 0.1 degrees, Global, EXPERIMENTAL";
        Float32 Westernmost_Easting 0.0;
    }
}
