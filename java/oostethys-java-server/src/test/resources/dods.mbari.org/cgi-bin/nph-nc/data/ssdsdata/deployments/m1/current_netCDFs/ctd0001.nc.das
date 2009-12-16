Attributes {
    esecs {
        String long_name "time GMT";
        String units "seconds since 1970-01-01 00:00:00";
        String point_spacing "uneven";
    }
    NominalDepth {
        String long_name "Depth";
        String units "m";
        String standard_name "depth";
    }
    Longitude {
        String long_name "Longitude";
        String units "degrees_east";
        String standard_name "longitude";
    }
    Latitude {
        String long_name "Latitude";
        String units "degrees_north";
        String standard_name "latitude";
    }
    Temperature {
        String long_name "Water Temperature";
        String units "deg C";
        String standard_name "sea_water_temperature";
    }
    Conductivity {
        String long_name "Conductivity";
        String units "S/m";
        String standard_name "sea_water_electrical_conductivity";
    }
    Salinity {
        String long_name "Salinity";
        String units "";
        String standard_name "sea_water_salinity";
    }
    NC_GLOBAL {
        String Unlimited_Dimension "time";
        String naming_authority "org.mbari.ssds.DataContainer.Name";
        String SIAM_metadata "http://new-ssds.mbari.org:8080/servlet/GetOriginalDataServlet?deviceID=1418&startTimestampSeconds=1223496640&recordTypeID=0&noHTMLHeader=1&isi=1";
        String standard_name_vocabulary "CF-1.0";
        String keywords "ocean; Moorings;";
        String cdm_data_type "Station";
        String geospatial_vertical_positive "down";
        String summary "Observational oceanographic data obtained from a moored platform.  Sensors placed on the mooring record environmental parameters as time passes.";
        String keywords_vocabulary "GCMD";
        String title "Mooring M1 CTD data from MBARI instrument id 1418 at original sampling intervals";
        String Conventions "CF-1.0";
        String creator_name "DStoNetCDF.pl version 1.47";
        String time_coverage_start "2008-10-08T20:10:40Z";
        String time_coverage_end "2009-06-11T10:56:46Z";
        String date_created "Thu Jun 11 04:03:39 2009";
        String id "m1_ctd0001_20081008_original.nc";
    }
}
