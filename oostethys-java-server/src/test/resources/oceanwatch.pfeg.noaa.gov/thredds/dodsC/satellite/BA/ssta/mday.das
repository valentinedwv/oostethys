Attributes {
    altitude {
        Float64 actual_range 0.0, 0.0;
        Int32 fraction_digits 0;
        String long_name "Altitude";
        String positive "up";
        String standard_name "altitude";
        String units "m";
        String axis "Z";
        String _CoordinateAxisType "Height";
        String _CoordinateZisPositive "up";
    }
    lat {
        String _CoordinateAxisType "Lat";
        Float64 actual_range -75.0, 75.0;
        String coordsys "geographic";
        Int32 fraction_digits 1;
        String long_name "Latitude";
        String point_spacing "even";
        String standard_name "latitude";
        String units "degrees_north";
        String axis "Y";
    }
    lon {
        String _CoordinateAxisType "Lon";
        Float64 actual_range 0.0, 360.0;
        String coordsys "geographic";
        Int32 fraction_digits 1;
        String long_name "Longitude";
        String point_spacing "even";
        String standard_name "longitude";
        String units "degrees_east";
        String axis "X";
    }
    time {
        Float64 actual_range 1.23984E9, 1.23984E9;
        Int32 fraction_digits 0;
        String long_name "Centered Time";
        String units "seconds since 1970-01-01T00:00:00Z";
        String standard_name "time";
        String axis "T";
        String _CoordinateAxisType "Time";
    }
    BAssta {
        Float32 _FillValue -9999999.0;
        Float32 actual_range -2.99, 33.4718;
        String coordsys "geographic";
        Int32 fraction_digits 1;
        String long_name "SST, Blended, 0.1 degrees, Global, EXPERIMENTAL";
        Float32 missing_value -9999999.0;
        Int32 numberOfObservations 3456923;
        Float64 percentCoverage 0.6395667722027766;
        String standard_name "sea_surface_temperature";
        String units "degree_C";
    }
    NC_GLOBAL {
        String acknowledgement "NOAA NESDIS COASTWATCH, NOAA SWFSC ERD";
        String cdm_data_type "Grid";
        Int32 cols 3601;
        String composite "true";
        String contributor_name "Remote Sensing Systems Inc, JAXA, NASA, OSDPD, CoastWatch";
        String contributor_role "Source of level 2 data.";
        String Conventions "COARDS, CF-1.0, Unidata Dataset Discovery v1.0, CWHDF";
        String creator_email "dave.foley@noaa.gov";
        String creator_name "NOAA CoastWatch, West Coast Node";
        String creator_url "http://coastwatch.pfel.noaa.gov";
        String cwhdf_version "3.4";
        String date_created "2009-05-01Z";
        String date_issued "2009-05-01Z";
        Float64 Easternmost_Easting 360.0;
        Float64 et_affine 0.0, 0.1, 0.1, 0.0, 0.0, -75.0;
        Int32 gctp_datum 12;
        Float64 gctp_parm 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0;
        Int32 gctp_sys 0;
        Int32 gctp_zone 0;
        Float64 geospatial_lat_max 75.0;
        Float64 geospatial_lat_min -75.0;
        Float64 geospatial_lat_resolution 0.1;
        String geospatial_lat_units "degrees_north";
        Float64 geospatial_lon_max 360.0;
        Float64 geospatial_lon_min 0.0;
        Float64 geospatial_lon_resolution 0.1;
        String geospatial_lon_units "degrees_east";
        Float64 geospatial_vertical_max 0.0;
        Float64 geospatial_vertical_min 0.0;
        String geospatial_vertical_positive "up";
        String geospatial_vertical_units "m";
        String history "Remote Sensing Systems Inc, JAXA, NASA, OSDPD, CoastWatch
2009-05-01T22:30:56Z NOAA CoastWatch (West Coast Node) and NOAA SFSC ERD";
        String id "LBAsstaSmday_20090416000000";
        String institution "NOAA CoastWatch, West Coast Node";
        String keywords "EARTH SCIENCE > Oceans > Ocean Temperature > Sea Surface Temperature";
        String keywords_vocabulary "GCMD Science Keywords";
        String license "The data may be used and redistributed for free but is not intended for legal use, since it may contain inaccuracies. Neither the data Contributor, CoastWatch, NOAA, nor the United States Government, nor any of their employees or contractors, makes any warranty, express or implied, including warranties of merchantability and fitness for a particular purpose, or assumes any legal liability for the accuracy, completeness, or usefulness, of this information.";
        String naming_authority "gov.noaa.pfel.coastwatch";
        Float64 Northernmost_Northing 75.0;
        String origin "Remote Sensing Systems Inc, JAXA, NASA, OSDPD, CoastWatch";
        Int32 pass_date 14335, 14336, 14337, 14338, 14339, 14340, 14341, 14342, 14343, 14344, 14345, 14346, 14347, 14348, 14349, 14350, 14351, 14352, 14353, 14354, 14355, 14356, 14357, 14358, 14359, 14360, 14361, 14362, 14363, 14364;
        Float64 polygon_latitude -75.0, 75.0, 75.0, -75.0, -75.0;
        Float64 polygon_longitude 0.0, 0.0, 360.0, 360.0, 0.0;
        String processing_level "3";
        String project "CoastWatch (http://coastwatch.noaa.gov/)";
        String projection "geographic";
        String projection_type "mapped";
        String references "Blended SST from satellites information: This is an experimental product which blends satellite-derived SST data from multiple platforms using a weighted mean.  Weights are based on the inverse square of the nominal accuracy of each satellite. AMSR_E Processing information: http://www.ssmi.com/amsr/docs/AMSRE_V05_Updates.pdf . AMSR-E Processing reference: Wentz, F.J., C. Gentemann, and P. Ashcroft. 2005. ON-ORBIT CALIBRATION OF AMSR-E AND THE RETRIEVAL OF OCEAN PRODUCTS. Remote Sensing Systems Internal Report. AVHRR Processing Information: http://www.osdpd.noaa.gov/PSB/EPS/CW/coastwatch.html .  AVHRR Processing Reference: Walton C. C., W. G. Pichel, J. F. Sapper, D. A. May. The development and operational application of nonlinear algorithms for the measurement of sea surface temperatures with the NOAA polar-orbiting environmental satellites. J.G.R., 103: (C12) 27999-28012, 1998. Cloudmask reference: Stowe, L. L., P. A. Davis, and E. P. McClain.  Scientific basis and initial evaluation of the CLAVR-1 global clear/cloud classification algorithm for the advanced very high resolution radiometer. J. Atmos. Oceanic Technol., 16, 656-681. 1999. Calibration and Validation: Li, X., W. Pichel, E. Maturi, P. Clemente-Colon, and J. Sapper. Deriving the operational nonlinear multi-channel sea surface temperature algorithm coefficients for NOAA-15 AVHRR/3. International Journal of Remote Sensing, Volume 22, No. 4, 699 - 704, March 2001a. Calibration and Validation: Li, X, W. Pichel, P. Clemente-Colon, V. Krasnopolsky, and J. Sapper. Validation of coastal sea and lake surface temperature measurements derived from NOAA/AVHRR Data. International Journal of Remote Sensing, Vol. 22, No. 7, 1285-1303, 2001b. GOES Imager Processing Information: http://coastwatch.noaa.gov/goes_sst_overview.html .  GOES Imager Processing Reference: Wu, X., W. P. Menzel, and G. S. Wade, 1999. Estimation of sea surface temperatures using GOES-8/9 radiance measurements, Bull. Amer. Meteor. Soc., 80, 1127-1138.  MODIS Aqua Processing Information: http://oceancolor.gsfc.nasa.gov/DOCS/modis_sst/ . MODIS Aqua Processing reference: Not Available.";
        Int32 rows 1501;
        String satellite "Aqua, GOES, POES";
        String sensor "AMSR-E, MODIS, Imager, AVHRR";
        String source "satellite observation: Aqua, GOES, POES, AMSR-E, MODIS, Imager, AVHRR";
        Float64 Southernmost_Northing -75.0;
        String standard_name_vocabulary "CF-1.0";
        Float64 start_time 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0;
        String summary "NOAA OceanWatch provides a blended sea surface temperature (SST) products derived from both microwave and infrared sensors carried on multiple platforms.  The microwave instruments can measure ocean temperatures even in the presence of clouds, though the resolution is a bit coarse when considering features typical of the coastal environment.  These are complemented by the relatively fine measurements of infrared sensors.  The blended data are provided at moderate spatial resolution (0.1 degrees) for the Global Ocean.  Measurements are gathered by Japan's Advanced Microwave Scanning Radiometer (AMSR-E) instrument, a passive radiance sensor carried aboard NASA's Aqua spacecraft, NOAA's Advanced Very High Resolution Radiometer, NOAA GOES Imager, and NASA's Moderate Resolution Imaging Spectrometer (MODIS). THIS IS AN EXPERIMENTAL PRODUCT: intended strictly for scientific evaluation by professional marine scientists.";
        String time_coverage_end "2009-05-01T00:00:00Z";
        String time_coverage_start "2009-04-01T00:00:00Z";
        String title "SST, Blended, 0.1 degrees, Global, EXPERIMENTAL";
        Float64 Westernmost_Easting 0.0;
    }
}
