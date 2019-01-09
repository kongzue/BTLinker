/*
 * Copyright Cypress Semiconductor Corporation, 2014-2015 All rights reserved.
 * 
 * This software, associated documentation and materials ("Software") is
 * owned by Cypress Semiconductor Corporation ("Cypress") and is
 * protected by and subject to worldwide patent protection (UnitedStates and foreign), United States copyright laws and international
 * treaty provisions. Therefore, unless otherwise specified in a separate license agreement between you and Cypress, this Software
 * must be treated like any other copyrighted material. Reproduction,
 * modification, translation, compilation, or representation of this
 * Software in any other form (e.g., paper, magnetic, optical, silicon)
 * is prohibited without Cypress's express written permission.
 * 
 * Disclaimer: THIS SOFTWARE IS PROVIDED AS-IS, WITH NO WARRANTY OF ANY
 * KIND, EXPRESS OR IMPLIED, INCLUDING, BUT NOT LIMITED TO,
 * NONINFRINGEMENT, IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE. Cypress reserves the right to make changes
 * to the Software without notice. Cypress does not assume any liability
 * arising out of the application or use of Software or any product or
 * circuit described in the Software. Cypress does not authorize its
 * products for use as critical components in any products where a
 * malfunction or failure may reasonably be expected to result in
 * significant injury or death ("High Risk Product"). By including
 * Cypress's product in a High Risk Product, the manufacturer of such
 * system or application assumes all risk of such use and in doing so
 * indemnifies Cypress against all liability.
 * 
 * Use of this Software may be limited by and subject to the applicable
 * Cypress software license agreement.
 * 
 * 
 */

package com.kongzue.btutil.util;

import java.util.HashMap;

/**
 * This class includes a subset of standard GATT attributes and carousel image
 * mapping
 */
public class GattAttributes {

    /**
     * Services
     */
    //有人服务
    public static final String USR_SERVICE = "0003cdd0-0000-1000-8000-00805f9b0131";

    public static final String HEART_RATE_SERVICE = "0000180d-0000-1000-8000-00805f9b34fb";
    public static final String DEVICE_INFORMATION_SERVICE = "0000180a-0000-1000-8000-00805f9b34fb";
    public static final String HEALTH_TEMP_SERVICE = "00001809-0000-1000-8000-00805f9b34fb";
    public static final String BATTERY_SERVICE = "0000180f-0000-1000-8000-00805f9b34fb";
    public static final String IMMEDIATE_ALERT_SERVICE = "00001802-0000-1000-8000-00805f9b34fb";
    public static final String CAPSENSE_SERVICE = "0000cab5-0000-1000-8000-00805f9b34fb";
    public static final String CAPSENSE_SERVICE_CUSTOM = "0003cab5-0000-1000-8000-00805f9b0131";
    public static final String RGB_LED_SERVICE = "0000cbbb-0000-1000-8000-00805f9b34fb";
    public static final String RGB_LED_SERVICE_CUSTOM = "0003cbbb-0000-1000-8000-00805f9b0131";
    public static final String LINK_LOSS_SERVICE = "00001803-0000-1000-8000-00805f9b34fb";
    public static final String TRANSMISSION_POWER_SERVICE = "00001804-0000-1000-8000-00805f9b34fb";
    public static final String BLOOD_PRESSURE_SERVICE = "00001810-0000-1000-8000-00805f9b34fb";
    public static final String GLUCOSE_SERVICE = "00001808-0000-1000-8000-00805f9b34fb";
    public static final String RSC_SERVICE = "00001814-0000-1000-8000-00805f9b34fb";
    public static final String BAROMETER_SERVICE = "00040001-0000-1000-8000-00805f9b0131";
    public static final String ACCELEROMETER_SERVICE = "00040020-0000-1000-8000-00805f9b0131";
    public static final String ANALOG_TEMPERATURE_SERVICE = "00040030-0000-1000-8000-00805f9b0131";
    public static final String CSC_SERVICE = "00001816-0000-1000-8000-00805f9b34fb";
    public static final String HUMAN_INTERFACE_DEVICE_SERVICE = "00001812-0000-1000-8000-00805f9b34fb";
    public static final String SCAN_PARAMETERS_SERVICE = "00001813-0000-1000-8000-00805f9b34fb";
    // public static final String OTA_UPDATE_SERVICE = "00060000-0000-1000-8000-00805f9b34fb";
    public static final String OTA_UPDATE_SERVICE = "00060000-f8ce-11e4-abf4-0002a5d5c51b";
    /**
     * Unused service UUIDS
     */
    public static final String ALERT_NOTIFICATION_SERVICE = "00001811-0000-1000-8000-00805f9b34fb";
    public static final String BODY_COMPOSITION_SERVICE = "0000181b-0000-1000-8000-00805f9b34fb";
    public static final String BODY_MANAGEMENT_SERVICE = "0000181e-0000-1000-8000-00805f9b34fb";
    public static final String CONTINUOUS_GLUCOSE_MONITORING_SERVICE = "0000181f-0000-1000-8000-00805f9b34fb";
    public static final String CURRENT_TIME_SERVICE = "00001805-0000-1000-8000-00805f9b34fb";
    public static final String CYCLING_POWER_SERVICE = "00001818-0000-1000-8000-00805f9b34fb";
    public static final String ENVIRONMENTAL_SENSING_SERVICE = "0000181a-0000-1000-8000-00805f9b34fb";
    public static final String LOCATION_NAVIGATION_SERVICE = "00001819-0000-1000-8000-00805f9b34fb";
    public static final String NEXT_DST_CHANGE_SERVICE = "00001807-0000-1000-8000-00805f9b34fb";
    public static final String PHONE_ALERT_STATUS_SERVICE = "0000180e-0000-1000-8000-00805f9b34fb";
    public static final String REFERENCE_TIME_UPDATE_SERVICE = "00001806-0000-1000-8000-00805f9b34fb";
    public static final String USER_DATA_SERVICE = "0000181c-0000-1000-8000-00805f9b34fb";
    public static final String WEIGHT_SCALE_SERVICE = "0000181d-0000-1000-8000-00805f9b34fb";
    /**
     * Heart rate characteristics
     */
    public static final String HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb";
    public static final String BODY_SENSOR_LOCATION = "00002a38-0000-1000-8000-00805f9b34fb";
    /**
     * Device information characteristics
     */
    public static final String SYSTEM_ID = "00002a23-0000-1000-8000-00805f9b34fb";
    public static final String MODEL_NUMBER_STRING = "00002a24-0000-1000-8000-00805f9b34fb";
    public static final String SERIAL_NUMBER_STRING = "00002a25-0000-1000-8000-00805f9b34fb";
    public static final String FIRMWARE_REVISION_STRING = "00002a26-0000-1000-8000-00805f9b34fb";
    public static final String HARDWARE_REVISION_STRING = "00002a27-0000-1000-8000-00805f9b34fb";
    public static final String SOFTWARE_REVISION_STRING = "00002a28-0000-1000-8000-00805f9b34fb";
    public static final String MANUFACTURER_NAME_STRING = "00002a29-0000-1000-8000-00805f9b34fb";
    public static final String PNP_ID = "00002a50-0000-1000-8000-00805f9b34fb";
    public static final String IEEE = "00002a2a-0000-1000-8000-00805f9b34fb";
    /**
     * Battery characteristics
     */
    public static final String BATTERY_LEVEL = "00002a19-0000-1000-8000-00805f9b34fb";
    /**
     * Health Thermometer characteristics
     */
    public static final String HEALTH_TEMP_MEASUREMENT = "00002a1c-0000-1000-8000-00805f9b34fb";
    public static final String TEMPERATURE_TYPE = "00002a1d-0000-1000-8000-00805f9b34fb";
    /**
     * Gatt services
     */
    public static final String GENERIC_ACCESS_SERVICE = "00001800-0000-1000-8000-00805f9b34fb";
    public static final String GENERIC_ATTRIBUTE_SERVICE = "00001801-0000-1000-8000-00805f9b34fb";
    /**
     * Find me characteristics
     */
    public static final String ALERT_LEVEL = "00002a06-0000-1000-8000-00805f9b34fb";
    public static final String TRANSMISSION_POWER_LEVEL = "00002a07-0000-1000-8000-00805f9b34fb";
    /**
     * Capsense characteristics
     */
    public static final String CAPSENSE_PROXIMITY = "0000caa1-0000-1000-8000-00805f9b34fb";
    public static final String CAPSENSE_SLIDER = "0000caa2-0000-1000-8000-00805f9b34fb";
    public static final String CAPSENSE_BUTTONS = "0000caa3-0000-1000-8000-00805f9b34fb";
    public static final String CAPSENSE_PROXIMITY_CUSTOM = "0003caa1-0000-1000-8000-00805f9b0131";
    public static final String CAPSENSE_SLIDER_CUSTOM = "0003caa2-0000-1000-8000-00805f9b0131";
    public static final String CAPSENSE_BUTTONS_CUSTOM = "0003caa3-0000-1000-8000-00805f9b0131";
    /**
     * RGB characteristics
     */
    public static final String RGB_LED = "0000cbb1-0000-1000-8000-00805f9b34fb";
    public static final String RGB_LED_CUSTOM = "0003cbb1-0000-1000-8000-00805f9b0131";
    /**
     * GlucoseService characteristics
     */
    public static final String GLUCOSE_COCNTRN = "00002a18-0000-1000-8000-00805f9b34fb";
    /**
     * Blood Pressure service Characteristics
     */
    public static final String BLOOD_PRESSURE_MEASUREMENT = "00002a35-0000-1000-8000-00805f9b34fb";
    /**
     * Running Speed & Cadence Characteristics
     */
    public static final String RSC_MEASUREMENT = "00002a53-0000-1000-8000-00805f9b34fb";
    /**
     * Cycling Speed & Cadence Characteristics
     */
    public static final String CSC_MEASUREMENT = "00002a5b-0000-1000-8000-00805f9b34fb";
    /**
     * Barometer service characteristics
     */
    public static final String BAROMETER_DIGITAL_SENSOR = "00040002-0000-1000-8000-00805f9b0131";
    public static final String BAROMETER_SENSOR_SCAN_INTERVAL = "00040004-0000-1000-8000-00805f9b0131";
    public static final String BAROMETER_DATA_ACCUMULATION = "00040007-0000-1000-8000-00805f9b0131";
    public static final String BAROMETER_READING = "00040009-0000-1000-8000-00805f9b0131";
    public static final String BAROMETER_THRESHOLD_FOR_INDICATION = "0004000d-0000-1000-8000-00805f9b0131";
    /**
     * Accelerometer service characteristics
     */
    public static final String ACCELEROMETER_ANALOG_SENSOR = "00040021-0000-1000-8000-00805f9b0131";
    public static final String ACCELEROMETER_SENSOR_SCAN_INTERVAL = "00040023-0000-1000-8000-00805f9b0131";
    public static final String ACCELEROMETER_DATA_ACCUMULATION = "00040026-0000-1000-8000-00805f9b0131";
    public static final String ACCELEROMETER_READING_X = "00040028-0000-1000-8000-00805f9b0131";
    public static final String ACCELEROMETER_READING_Y = "0004002b-0000-1000-8000-00805f9b0131";
    public static final String ACCELEROMETER_READING_Z = "0004002d-0000-1000-8000-00805f9b0131";
    /**
     * Analog Temperature service characteristics
     */
    public static final String TEMPERATURE_ANALOG_SENSOR = "00040031-0000-1000-8000-00805f9b0131";
    public static final String TEMPERATURE_SENSOR_SCAN_INTERVAL = "00040032-0000-1000-8000-00805f9b0131";
    public static final String TEMPERATURE_READING = "00040033-0000-1000-8000-00805f9b0131";
    /**
     * HID Characteristics
     */
    public static final String PROTOCOL_MODE = "00002a4e-0000-1000-8000-00805f9b34fb";
    public static final String REP0RT = "00002a4d-0000-1000-8000-00805f9b34fb";
    public static final String REPORT_MAP = "00002a4b-0000-1000-8000-00805f9b34fb";
    public static final String BOOT_KEYBOARD_INPUT_REPORT = "00002a22-0000-1000-8000-00805f9b34fb";
    public static final String BOOT_KEYBOARD_OUTPUT_REPORT = "00002a32-0000-1000-8000-00805f9b34fb";
    public static final String BOOT_MOUSE_INPUT_REPORT = "00002a33-0000-1000-8000-00805f9b34fb";
    public static final String HID_CONTROL_POINT = "00002a4c-0000-1000-8000-00805f9b34fb";
    public static final String HID_INFORMATION = "00002a4a-0000-1000-8000-00805f9b34fb";
    /**
     * OTA Characteristic
     */
    //public static final String OTA_CHARACTERISTIC = "00060001-0000-1000-8000-00805F9B34fb";
    public static final String OTA_CHARACTERISTIC = "00060001-f8ce-11e4-abf4-0002a5d5c51b";
    /**
     * Descriptor UUID's
     */
    public static final String CHARACTERISTIC_EXTENDED_PROPERTIES = "00002900-0000-1000-8000-00805f9b34fb";
    public static final String CHARACTERISTIC_USER_DESCRIPTION = "00002901-0000-1000-8000-00805f9b34fb";
    public static final String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    public static final String SERVER_CHARACTERISTIC_CONFIGURATION = "00002903-0000-1000-8000-00805f9b34fb";
    public static final String CHARACTERISTIC_PRESENTATION_FORMAT = "00002904-0000-1000-8000-00805f9b34fb";
    public static final String CHARACTERISTIC_AGGREGATE_FORMAT = "00002905-0000-1000-8000-00805f9b34fb";
    public static final String VALID_RANGE = "00002906-0000-1000-8000-00805f9b34fb";
    public static final String EXTERNAL_REPORT_REFERENCE = "00002907-0000-1000-8000-00805f9b34fb";
    public static final String REPORT_REFERENCE = "00002908-0000-1000-8000-00805f9b34fb";
    public static final String ENVIRONMENTAL_SENSING_CONFIGURATION = "0000290B-0000-1000-8000-00805f9b34fb";
    public static final String ENVIRONMENTAL_SENSING_MEASUREMENT = "0000290C-0000-1000-8000-00805f9b34fb";
    public static final String ENVIRONMENTAL_SENSING_TRIGGER_SETTING = "0000290D-0000-1000-8000-00805f9b34fb";
    private static final HashMap<String, Integer> attributesImageMap = new HashMap<String, Integer>();
    private static final HashMap<String, Integer> attributesCapSenseImageMap = new HashMap<String, Integer>();
    private static final HashMap<String, String> attributesCapSense = new HashMap<String, String>();
    private static final String HEALTH_THERMO_SERVICE = "00001809-0000-1000-8000-00805f9b34fb";
    private static final String BOND_MANAGEMENT_SERVICE = "0000181e-0000-1000-8000-00805f9b34fb";
    private static final String HEART_RATE_CONTROL_POINT = "00002a39-0000-1000-8000-00805f9b34fb";
    private static final String TEMPERATURE_INTERMEDIATE = "00002a1e-0000-1000-8000-00805f9b34fb";
    private static final String TEMPERATURE_MEASUREMENT_INTERVAL = "00002a21-0000-1000-8000-00805f9b34fb";
    private static final String GLUCOSE_MESUREMENT_CONTEXT = "00002a34-0000-1000-8000-00805f9b34fb";
    private static final String GLUCOSE_FEATURE = "00002a51-0000-1000-8000-00805f9b34fb";
    private static final String GLUCOSE_MESUREMENT = "00002a18-0000-1000-8000-00805f9b34fb";
    private static final String RECORD_ACCESS_CONTROL_POINT = "00002a52-0000-1000-8000-00805f9b34fb";
    private static final String BLOOD_INTERMEDIATE_CUFF_PRESSURE = "00002a36-0000-1000-8000-00805f9b34fb";
    private static final String BLOOD_PRESSURE_FEATURE = "00002a49-0000-1000-8000-00805f9b34fb";
    private static final String RSC_FEATURE = "00002a54-0000-1000-8000-00805f9b34fb";
    private static final String SC_SENSOR_LOCATION = "00002a5d-0000-1000-8000-00805f9b34fb";
    private static final String SC_CONTROL_POINT = "00002a55-0000-1000-8000-00805f9b34fb";
    private static final String CSC_FEATURE = "00002a5c-0000-1000-8000-00805f9b34fb";
    /**
     * Unused Service characteristics
     */
    private static final String AEROBIC_HEART_RATE_LOWER_LIMIT = "00002a7e-0000-1000-8000-00805f9b34fb";
    private static final String AEROBIC_HEART_RATE_UPPER_LIMIT = "00002a84-0000-1000-8000-00805f9b34fb";
    private static final String AGE = "00002a80-0000-1000-8000-00805f9b34fb";
    private static final String ALERT_CATEGORY_ID = "00002a43-0000-1000-8000-00805f9b34fb";
    private static final String ALERT_CATEGORY_ID_BIT_MASK = "00002a42-0000-1000-8000-00805f9b34fb";
    private static final String ALERT_STATUS = "00002a3F-0000-1000-8000-00805f9b34fb";
    private static final String ANAEROBIC_HEART_RATE_LOWER_LIMIT = "00002a81-0000-1000-8000-00805f9b34fb";
    private static final String ANAEROBIC_HEART_RATE_UPPER_LIMIT = "00002a82-0000-1000-8000-00805f9b34fb";
    private static final String ANAEROBIC_THRESHOLD = "00002aA83-0000-1000-8000-00805f9b34fb";
    private static final String APPARENT_WIND_DIRECTION = "00002a73-0000-1000-8000-00805f9b34fb";
    private static final String APPARENT_WIND_SPEED = "00002a72-0000-1000-8000-00805f9b34fb";
    private static final String APPEARANCE = "00002a01-0000-1000-8000-00805f9b34fb";
    private static final String BAROMETRIC_PRESSURE_TREND = "00002aa3-0000-1000-8000-00805f9b34fb";
    private static final String BODY_COMPOSITION_FEATURE = "00002a9B-0000-1000-8000-00805f9b34fb";
    private static final String BODY_COMPOSITION_MEASUREMENT = "00002a9C-0000-1000-8000-00805f9b34fb";
    private static final String BOND_MANAGEMENT_CONTROL_POINT = "00002aa4-0000-1000-8000-00805f9b34fb";
    private static final String BOND_MANAGEMENT_FEATURE = "00002aa5-0000-1000-8000-00805f9b34fb";
    private static final String CENTRAL_ADDRESS_RESOLUTION = "00002aa6-0000-1000-8000-00805f9b34fb";
    private static final String CGM_FEATURE = "00002aa8-0000-1000-8000-00805f9b34fb";
    private static final String CGM_MEASUREMENT = "00002aa7-0000-1000-8000-00805f9b34fb";
    private static final String CGM_SESSION_RUN_TIME = "00002aab-0000-1000-8000-00805f9b34fb";
    private static final String CGM_SESSION_START_TIME = "00002aaa-0000-1000-8000-00805f9b34fb";
    private static final String CGM_SPECIFIC_OPS_CONTROL_POINT = "00002aaC-0000-1000-8000-00805f9b34fb";
    private static final String CGM_STATUS = "00002aa9-0000-1000-8000-00805f9b34fb";
    private static final String CYCLING_POWER_CONTROL_POINT = "00002a66-0000-1000-8000-00805f9b34fb";
    private static final String CYCLING_POWER_FEATURE = "00002a65-0000-1000-8000-00805f9b34fb";
    private static final String CYCLING_POWER_MEASUREMENT = "00002a63-0000-1000-8000-00805f9b34fb";
    private static final String CYCLING_POWER_VECTOR = "00002a64-0000-1000-8000-00805f9b34fb";
    private static final String DATABASE_CHANGE_INCREMENT = "00002a99-0000-1000-8000-00805f9b34fb";
    private static final String DATE_OF_BIRTH = "00002a85-0000-1000-8000-00805f9b0131";
    private static final String DATE_OF_THRESHOLD_ASSESSMENT = "00002a86-0000-1000-8000-00805f9b0131";
    private static final String DATE_TIME = "00002a08-0000-1000-8000-00805f9b34fb";
    private static final String DAY_DATE_TIME = "00002a0a-0000-1000-8000-00805f9b34fb";
    private static final String DAY_OF_WEEK = "00002A09-0000-1000-8000-00805f9b34fb";
    private static final String DESCRIPTOR_VALUE_CHANGED = "00002a7d-0000-1000-8000-00805f9b34fb";
    private static final String DEVICE_NAME = "00002a00-0000-1000-8000-00805f9b34fb";
    private static final String DEW_POINT = "00002a7b-0000-1000-8000-00805f9b34fb";
    private static final String DST_OFFSET = "00002a0d-0000-1000-8000-00805f9b34fb";
    private static final String ELEVATION = "00002a6c-0000-1000-8000-00805f9b34fb";
    private static final String EMAIL_ADDRESS = "00002a87-0000-1000-8000-00805f9b34fb";
    private static final String EXACT_TIME_256 = "00002a0c-0000-1000-8000-00805f9b34fb";
    private static final String FAT_BURN_HEART_RATE_LOWER_LIMIT = "00002a88-0000-1000-8000-00805f9b34fb";
    private static final String FAT_BURN_HEART_RATE_UPPER_LIMIT = "00002a89-0000-1000-8000-00805f9b34fb";
    private static final String FIRSTNAME = "00002a8a-0000-1000-8000-00805f9b34fb";
    private static final String FIVE_ZONE_HEART_RATE_LIMITS = "00002A8b-0000-1000-8000-00805f9b34fb";
    private static final String GENDER = "00002a8c-0000-1000-8000-00805f9b34fb";
    private static final String GUST_FACTOR = "00002a74-0000-1000-8000-00805f9b34fb";
    private static final String HEAT_INDEX = "00002a89-0000-1000-8000-00805f9b34fb";
    private static final String HEIGHT = "00002a8a-0000-1000-8000-00805f9b34fb";
    private static final String HEART_RATE_MAX = "00002a8d-0000-1000-8000-00805f9b34fb";
    private static final String HIP_CIRCUMFERENCE = "00002a8f-0000-1000-8000-00805f9b34fb";
    private static final String HUMIDITY = "00002a6f-0000-1000-8000-00805f9b34fb";
    private static final String INTERMEDIATE_CUFF_PRESSURE = "00002a36-0000-1000-8000-00805f9b34fb";
    private static final String INTERMEDIATE_TEMPERATURE = "00002a1e-0000-1000-8000-00805f9b34fb";
    private static final String IRRADIANCE = "00002a77-0000-1000-8000-00805f9b34fb";
    private static final String LANGUAGE = "00002aa2-0000-1000-8000-00805f9b34fb";
    private static final String LAST_NAME = "00002a90-0000-1000-8000-00805f9b34fb";
    private static final String LN_CONTROL_POINT = "00002a6b-0000-1000-8000-00805f9b34fb";
    private static final String LN_FEATURE = "00002a6a-0000-1000-8000-00805f9b34fb";
    private static final String LOCAL_TIME_INFORMATION = "00002a0f-0000-1000-8000-00805f9b34fb";
    private static final String LOCATION_AND_SPEED = "00002a67-0000-1000-8000-00805f9b34fb";
    private static final String MAGNETIC_DECLINATION = "00002a2c-0000-1000-8000-00805f9b34fb";
    private static final String MAGNETIC_FLUX_DENSITY_2D = "00002aa0-0000-1000-8000-00805f9b34fb";
    private static final String MAGNETIC_FLUX_DENSITY_3D = "00002aa1-0000-1000-8000-00805f9b34fb";
    private static final String MANUFACTURE_NAME_STRING = "00002a29-0000-1000-8000-00805f9b34fb";
    private static final String MAXIMUM_RECOMMENDED_HEART_RATE = "00002a91-0000-1000-8000-00805f9b34fb";
    private static final String MEASUREMENT_INTERVAL = "00002a21-0000-1000-8000-00805f9b34fb";
    private static final String NAVIGATION = "00002a68-0000-1000-8000-00805f9b34fb";
    private static final String NEW_ALERT = "00002a46-0000-1000-8000-00805f9b34fb";
    private static final String PERIPHERAL_PREFERRED_CONNECTION_PARAMETERS = "00002a04-0000-1000-8000-00805f9b34fb";
    private static final String PERIPHERAL_PRIVACY_FLAG = "00002a02-0000-1000-8000-00805f9b34fb";
    private static final String POLLEN_CONCENTRATION = "00002a75-0000-1000-8000-00805f9b34fb";
    private static final String POSITION_QUALITY = "00002a69-0000-1000-8000-00805f9b34fb";
    private static final String PRESSURE = "00002a6d-0000-1000-8000-00805f9b34fb";
    public static String AEROBIC_THRESHOLD = "00002a7f-0000-1000-8000-00805f9b34fb";
    private static HashMap<String, String> attributes = new HashMap<String, String>();
    private static HashMap<String, String> descriptorAttributes = new HashMap<String, String>();

    static {

        // Services.
        attributes.put(USR_SERVICE, "USR Service");
        attributes.put(HEART_RATE_SERVICE, "Heart Rate Service");
        attributes.put(HEALTH_THERMO_SERVICE, "Health Thermometer Service");
        attributes.put(GENERIC_ACCESS_SERVICE, "Generic Access Service");
        attributes.put(GENERIC_ATTRIBUTE_SERVICE, "Generic Attribute Service");
        attributes
                .put(DEVICE_INFORMATION_SERVICE, "Device Information Service");
        attributes.put(BATTERY_SERVICE,// "0000180f-0000-1000-8000-00805f9b34fb",
                "Battery Service");
        attributes.put(IMMEDIATE_ALERT_SERVICE, "Immediate Alert");
        attributes.put(LINK_LOSS_SERVICE, "Link Loss");
        attributes.put(TRANSMISSION_POWER_SERVICE, "Tx Power");
        attributes.put(CAPSENSE_SERVICE, "CapSense Service");
        attributes.put(CAPSENSE_SERVICE_CUSTOM, "CapSense Service");
        attributes.put(RGB_LED_SERVICE, "RGB LED Service");
        attributes.put(RGB_LED_SERVICE_CUSTOM, "RGB LED Service");
        attributes.put(GLUCOSE_SERVICE, "Glucose Service");
        attributes.put(BLOOD_PRESSURE_SERVICE, "Blood Pressure Service");
        attributes.put(RSC_SERVICE, "Running Speed & Cadence Service");
        attributes.put(BAROMETER_SERVICE, "Barometer Service");
        attributes.put(ACCELEROMETER_SERVICE, "Accelerometer Service");
        attributes
                .put(ANALOG_TEMPERATURE_SERVICE, "Analog Temperature Service");
        attributes.put(CSC_SERVICE, "Cycling Speed & Cadence Service");

        // Unused Services
        attributes
                .put(ALERT_NOTIFICATION_SERVICE, "Alert notification Service");
        attributes.put(BODY_COMPOSITION_SERVICE, "Body Composition Service");
        attributes.put(BOND_MANAGEMENT_SERVICE, "Bond Management Service");
        attributes.put(CONTINUOUS_GLUCOSE_MONITORING_SERVICE,
                "Continuous Glucose Monitoring Service");
        attributes.put(CURRENT_TIME_SERVICE, "Current Time Service");
        attributes.put(CYCLING_POWER_SERVICE, "Cycling Power Service");
        attributes.put(ENVIRONMENTAL_SENSING_SERVICE,
                "Environmental Sensing Service");
        attributes.put(HUMAN_INTERFACE_DEVICE_SERVICE,
                "Human Interface device Service");
        attributes.put(LOCATION_NAVIGATION_SERVICE,
                "Location and Navigation Service");
        attributes.put(NEXT_DST_CHANGE_SERVICE, "Next DST Change Service");
        attributes
                .put(PHONE_ALERT_STATUS_SERVICE, "Phone Alert Status Service");
        attributes.put(REFERENCE_TIME_UPDATE_SERVICE,
                "Reference Time Update Service");
        attributes.put(SCAN_PARAMETERS_SERVICE, "Scan Paramenters Service");
        attributes.put(USER_DATA_SERVICE, "User Data Service");
        attributes.put(WEIGHT_SCALE_SERVICE, "Weight Scale Service");

        // Heart Rate Characteristics.
        attributes.put(HEART_RATE_MEASUREMENT, "Heart Rate Measurement");
        attributes.put(BODY_SENSOR_LOCATION, "Body Sensor Location");
        attributes.put(HEART_RATE_CONTROL_POINT, "Heart Rate Control Point");

        // Health thermometer Characteristics.
        attributes.put(HEALTH_TEMP_MEASUREMENT,
                "Health Thermometer Measurement");
        attributes.put(TEMPERATURE_TYPE, "Temperature Type");
        attributes.put(TEMPERATURE_INTERMEDIATE, "Intermediate Temperature");
        attributes
                .put(TEMPERATURE_MEASUREMENT_INTERVAL, "Measurement Interval");

        // Device Information Characteristics
        attributes.put(SYSTEM_ID, "System ID");
        attributes.put(MODEL_NUMBER_STRING, "Model Number String");
        attributes.put(SERIAL_NUMBER_STRING, "Serial Number String");
        attributes.put(FIRMWARE_REVISION_STRING, "Firmware Revision String");
        attributes.put(HARDWARE_REVISION_STRING, "Hardware Revision String");
        attributes.put(SOFTWARE_REVISION_STRING, "Software Revision String");
        attributes.put(MANUFACTURER_NAME_STRING, "Manufacturer Name String");
        attributes.put(PNP_ID, "PnP ID");
        attributes.put(IEEE,
                "IEEE 11073-20601 Regulatory Certification Data List");

        // Battery service characteristics
        attributes.put(BATTERY_LEVEL, "Battery Level");

        // Find me service characteristics
        attributes.put(ALERT_LEVEL, "Alert Level");
        attributes.put(TRANSMISSION_POWER_LEVEL, "Tx Power Level");

        // Capsense Characteristics
        attributes.put(CAPSENSE_BUTTONS, "CapSense Button");
        attributes.put(CAPSENSE_PROXIMITY, "CapSense Proximity");
        attributes.put(CAPSENSE_SLIDER, "CapSense Slider");
        attributes.put(CAPSENSE_BUTTONS_CUSTOM, "CapSense Button");
        attributes.put(CAPSENSE_PROXIMITY_CUSTOM, "CapSense Proximity");
        attributes.put(CAPSENSE_SLIDER_CUSTOM, "CapSense Slider");

        // RGB Characteristics
        attributes.put(RGB_LED, "RGB LED");
        attributes.put(RGB_LED_CUSTOM, "RGB LED");

        // Glucose Characteristics
        attributes.put(GLUCOSE_COCNTRN, "Glucose Measurement");
        attributes.put(GLUCOSE_MESUREMENT_CONTEXT,
                "Glucose Measurement Context");
        attributes.put(GLUCOSE_FEATURE, "Glucose Feature");
        attributes.put(RECORD_ACCESS_CONTROL_POINT,
                "Record Access Control Point");

        // Blood pressure service characteristics
        attributes.put(BLOOD_INTERMEDIATE_CUFF_PRESSURE,
                "Intermediate Cuff Pressure");
        attributes.put(BLOOD_PRESSURE_FEATURE, "Blood Pressure Feature");
        attributes
                .put(BLOOD_PRESSURE_MEASUREMENT, "Blood Pressure Measurement");

        // SensorHub Characteristics
        attributes.put(ACCELEROMETER_ANALOG_SENSOR,
                "Accelerometer Analog Sensor");
        attributes.put(ACCELEROMETER_DATA_ACCUMULATION,
                "Accelerometer Data Accumulation");
        attributes.put(ACCELEROMETER_READING_X, "Accelerometer X Reading");
        attributes.put(ACCELEROMETER_READING_Y, "Accelerometer Y Reading");
        attributes.put(ACCELEROMETER_READING_Z, "Accelerometer Z Reading");
        attributes.put(ACCELEROMETER_SENSOR_SCAN_INTERVAL,
                "Accelerometer Sensor Scan Interval");
        attributes.put(BAROMETER_DATA_ACCUMULATION,
                "Barometer Data Accumulation");
        attributes.put(BAROMETER_DIGITAL_SENSOR, "Barometer Digital Sensor");
        attributes.put(BAROMETER_READING, "Barometer Reading");
        attributes.put(BAROMETER_SENSOR_SCAN_INTERVAL,
                "Barometer Sensor Scan Interval");
        attributes.put(BAROMETER_THRESHOLD_FOR_INDICATION,
                "Barometer Threshold for Indication");
        attributes.put(TEMPERATURE_ANALOG_SENSOR, "Temperature Analog Sensor");
        attributes.put(TEMPERATURE_READING, "Temperature Reading");
        attributes.put(TEMPERATURE_SENSOR_SCAN_INTERVAL,
                "Temperature Sensor Scan Interval");

        //HID Characteristics
        attributes.put(PROTOCOL_MODE, "Protocol Mode");
        attributes.put(REP0RT, "Report");
        attributes.put(REPORT_MAP, "Report Map");
        attributes.put(BOOT_KEYBOARD_INPUT_REPORT, "Boot Keyboard Input Report");
        attributes.put(BOOT_KEYBOARD_OUTPUT_REPORT, "Boot Keyboard Output Report");
        attributes.put(BOOT_MOUSE_INPUT_REPORT, "Boot Mouse Input Report");
        attributes.put(HID_CONTROL_POINT, "HID Control Point");
        attributes.put(HID_INFORMATION, "HID Information");

        //OTA Characteristics
        attributes.put(OTA_UPDATE_SERVICE, "Bootloader Service");
        attributes.put(OTA_CHARACTERISTIC, "Bootloader Data Characteristic");

        // Unused Characteristics
        attributes.put(AEROBIC_HEART_RATE_LOWER_LIMIT,
                "Aerobic Heart Rate Lower Limit");
        attributes.put(AEROBIC_HEART_RATE_UPPER_LIMIT,
                "Aerobic Heart Rate Upper Limit");
        attributes.put(AGE, "Age");
        attributes.put(ALERT_CATEGORY_ID, "Alert Category Id");
        attributes
                .put(ALERT_CATEGORY_ID_BIT_MASK, "Alert Category_id_Bit_Mask");
        attributes.put(ALERT_STATUS, "Alert_Status");
        attributes.put(ANAEROBIC_HEART_RATE_LOWER_LIMIT,
                "Anaerobic Heart Rate Lower Limit");
        attributes.put(ANAEROBIC_HEART_RATE_UPPER_LIMIT,
                "Anaerobic Heart Rate Upper Limit");
        attributes.put(ANAEROBIC_THRESHOLD, "Anaerobic Threshold");
        attributes.put(APPARENT_WIND_DIRECTION, "Apparent Wind Direction");
        attributes.put(APPARENT_WIND_SPEED, "Apparent Wind Speed");
        attributes.put(APPEARANCE, "Appearance");
        attributes.put(BAROMETRIC_PRESSURE_TREND, "Barometric pressure Trend");
        attributes.put(BLOOD_PRESSURE_MEASUREMENT, "Blood Pressure Measurement");
        attributes.put(BODY_COMPOSITION_FEATURE, "Body Composition Feature");
        attributes.put(BODY_COMPOSITION_MEASUREMENT, "Body Composition Measurement");
        attributes.put(BOND_MANAGEMENT_CONTROL_POINT, "Bond Management Control Point");
        attributes.put(BOND_MANAGEMENT_FEATURE, "Bond Management feature");
        attributes.put(CGM_FEATURE, "CGM Feature");
        attributes.put(CENTRAL_ADDRESS_RESOLUTION, "Central Address Resolution");
        attributes.put(FIRSTNAME, "First Name");
        attributes.put(GUST_FACTOR, "Gust Factor");
        attributes.put(CGM_MEASUREMENT, "CGM Measurement");
        attributes.put(CGM_SESSION_RUN_TIME, "CGM Session Run Time");
        attributes.put(CGM_SESSION_START_TIME, "CGM Session Start Time");
        attributes.put(CGM_SPECIFIC_OPS_CONTROL_POINT, "CGM Specific Ops Control Point");
        attributes.put(CGM_STATUS, "CGM Status");
        attributes.put(CYCLING_POWER_CONTROL_POINT, "Cycling Power Control Point");
        attributes.put(CYCLING_POWER_VECTOR, "Cycling Power Vector");
        attributes.put(CYCLING_POWER_FEATURE, "Cycling Power Feature");
        attributes.put(CYCLING_POWER_MEASUREMENT, "Cycling Power Measurement");
        attributes.put(DATABASE_CHANGE_INCREMENT, "Database Change Increment");
        attributes.put(DATE_OF_BIRTH, "Date Of Birth");
        attributes.put(DATE_OF_THRESHOLD_ASSESSMENT, "Date Of Threshold Assessment");
        attributes.put(DATE_TIME, "Date Time");
        attributes.put(DAY_DATE_TIME, "Day Date Time");
        attributes.put(DAY_OF_WEEK, "Day Of Week");
        attributes.put(DESCRIPTOR_VALUE_CHANGED, "Descriptor Value Changed");
        attributes.put(DEVICE_NAME, "Device Name");
        attributes.put(DEW_POINT, "Dew Point");
        attributes.put(DST_OFFSET, "DST Offset");
        attributes.put(ELEVATION, "Elevation");
        attributes.put(EMAIL_ADDRESS, "Email Address");
        attributes.put(EXACT_TIME_256, "Exact Time 256");
        attributes.put(FAT_BURN_HEART_RATE_LOWER_LIMIT, "Fat Burn Heart Rate lower Limit");
        attributes.put(FAT_BURN_HEART_RATE_UPPER_LIMIT, "Fat Burn Heart Rate Upper Limit");
        attributes.put(FIRMWARE_REVISION_STRING, "Firmware Revision String");
        attributes.put(FIVE_ZONE_HEART_RATE_LIMITS, "Five Zone Heart Rate Limits");
        attributes.put(MANUFACTURE_NAME_STRING, "Manufacturer Name String");
        attributes.put(GENDER, "Gender");
        attributes.put(GLUCOSE_FEATURE, "Glucose Feature");
        attributes.put(GLUCOSE_MESUREMENT, "Glucose Measurement");
        attributes.put(HEART_RATE_MAX, "Heart Rate Max");
        attributes.put(HEAT_INDEX, "Heat Index");
        attributes.put(HEIGHT, "Height");
        attributes.put(HIP_CIRCUMFERENCE, "Hip Circumference");
        attributes.put(HUMIDITY, "Humidity");
        attributes.put(INTERMEDIATE_CUFF_PRESSURE, "Intermediate Cuff Pressure");
        attributes.put(INTERMEDIATE_TEMPERATURE, "Intermediate Temperature");
        attributes.put(IRRADIANCE, "Irradiance");
        attributes.put(LANGUAGE, "Language");
        attributes.put(LAST_NAME, "Last Name");
        attributes.put(LN_CONTROL_POINT, "LN Control Point");
        attributes.put(LN_FEATURE, "LN Feature");
        attributes.put(LOCAL_TIME_INFORMATION, "Local Time Information");
        attributes.put(LOCATION_AND_SPEED, "Location and Speed");
        attributes.put(MAGNETIC_DECLINATION, "Magenetic Declination");
        attributes.put(MAGNETIC_FLUX_DENSITY_2D, "Magentic Flux Density 2D");
        attributes.put(MAGNETIC_FLUX_DENSITY_3D, "Magentic Flux Density 3D");
        attributes.put(MAXIMUM_RECOMMENDED_HEART_RATE, "Maximum Recommended Heart Rate");
        attributes.put(MEASUREMENT_INTERVAL, "Measurement Interval");
        attributes.put(MODEL_NUMBER_STRING, "Model Number String");
        attributes.put(NEW_ALERT, "New Alert");
        attributes.put(NAVIGATION, "Navigation");
        attributes.put(PERIPHERAL_PREFERRED_CONNECTION_PARAMETERS, "Peripheral Preferred Connection Parameters");
        attributes.put(PERIPHERAL_PRIVACY_FLAG, "Peripheral Privacy Flag");
        attributes.put(POLLEN_CONCENTRATION, "Pollen Concentration");
        attributes.put(POSITION_QUALITY, "Position Quality");
        attributes.put(PRESSURE, "Pressure");


        // Descriptors
        attributes.put(CHARACTERISTIC_EXTENDED_PROPERTIES, "Characteristic Extended Properties");
        attributes.put(CHARACTERISTIC_USER_DESCRIPTION, "Characteristic User Description");
        attributes.put(CLIENT_CHARACTERISTIC_CONFIG, "Client Characteristic Configuration");
        attributes.put(SERVER_CHARACTERISTIC_CONFIGURATION, "Server Characteristic Configuration");
        attributes.put(CHARACTERISTIC_PRESENTATION_FORMAT, "Characteristic Presentation Format");
        attributes.put(CHARACTERISTIC_AGGREGATE_FORMAT, "Characteristic Aggregate Format");
        attributes.put(VALID_RANGE, "Valid Range");
        attributes.put(EXTERNAL_REPORT_REFERENCE, "External Report Reference");
        attributes.put(REPORT_REFERENCE, "Report Reference");
        attributes.put(ENVIRONMENTAL_SENSING_CONFIGURATION, "Environmental Sensing Configuration");
        attributes.put(ENVIRONMENTAL_SENSING_MEASUREMENT, "Environmental Sensing Measurement");
        attributes.put(ENVIRONMENTAL_SENSING_TRIGGER_SETTING, "Environmental Sensing Trigger Setting");

        // Running Speed Characteristics
        attributes.put(RSC_MEASUREMENT, "Running Speed and Cadence Measurement");
        attributes.put(RSC_FEATURE, "Running Speed and Cadence Feature");
        attributes.put(SC_CONTROL_POINT, "Speed and Cadence Control Point");
        attributes.put(SC_SENSOR_LOCATION, "Speed and Cadence Sensor Location");

        // Cycling Speed Characteristics
        attributes.put(CSC_MEASUREMENT, "Cycling Speed and Cadence Measurement");
        attributes.put(CSC_FEATURE, "Cycling Speed and Cadence Feature");


        // Capsense Characteristics
        attributesCapSense.put(CAPSENSE_SERVICE, "CapSense Services");
        attributesCapSense.put(CAPSENSE_SERVICE_CUSTOM, "CapSense Services");
        attributesCapSense.put(CAPSENSE_BUTTONS, "CapSense Button");
        attributesCapSense.put(CAPSENSE_BUTTONS_CUSTOM, "CapSense Button");
        attributesCapSense.put(CAPSENSE_PROXIMITY, "CapSense Proximity");
        attributesCapSense.put(CAPSENSE_PROXIMITY_CUSTOM, "CapSense Proximity");
        attributesCapSense.put(CAPSENSE_SLIDER, "CapSense Slider");
        attributesCapSense.put(CAPSENSE_SLIDER_CUSTOM, "CapSense Slider");

        /**
         * Descriptor key value mapping
         */

        descriptorAttributes.put("0", "Reserved For Future Use");
        descriptorAttributes.put("1", "Boolean");
        descriptorAttributes.put("2", "unsigned 2-bit integer");
        descriptorAttributes.put("3", "unsigned 4-bit integer");
        descriptorAttributes.put("4", "unsigned 8-bit integer");
        descriptorAttributes.put("5", "unsigned 12-bit integer");
        descriptorAttributes.put("6", "unsigned 16-bit integer");
        descriptorAttributes.put("7", "unsigned 24-bit integer");
        descriptorAttributes.put("8", "unsigned 32-bit integer");
        descriptorAttributes.put("9", "unsigned 48-bit integer");
        descriptorAttributes.put("10", "unsigned 64-bit integer");
        descriptorAttributes.put("11", "unsigned 128-bit integer");
        descriptorAttributes.put("12", "signed 8-bit integer");
        descriptorAttributes.put("13", "signed 12-bit integer");
        descriptorAttributes.put("14", "signed 16-bit integer");
        descriptorAttributes.put("15", "signed 24-bit integer");
        descriptorAttributes.put("16", "signed 32-bit integer");
        descriptorAttributes.put("17", "signed 48-bit integer");
        descriptorAttributes.put("18", "signed 64-bit integer");
        descriptorAttributes.put("19", "signed 128-bit integer");
        descriptorAttributes.put("20", "IEEE-754 32-bit floating point");
        descriptorAttributes.put("21", "IEEE-754 64-bit floating point");
        descriptorAttributes.put("22", "IEEE-11073 16-bit SFLOAT");
        descriptorAttributes.put("23", "IEEE-11073 32-bit FLOAT");
        descriptorAttributes.put("24", "IEEE-20601 format");
        descriptorAttributes.put("25", "UTF-8 string");
        descriptorAttributes.put("26", "UTF-16 string");
        descriptorAttributes.put("27", "Opaque Structure");

    }


    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }


    public static String lookupNameCapSense(String uuid, String defaultName) {
        String name = attributesCapSense.get(uuid);
        return name == null ? defaultName : name;
    }


    public static String lookCharacteristicPresentationFormat(String key) {
        String value = descriptorAttributes.get(key);
        return value == null ? "Reserved" : value;
    }


    public static boolean lookupreqHRMCharacateristics(String uuid) {
        String name = attributes.get(uuid);
        return name == null ? false : true;

    }

    public static String getname(String uuid) {
        String name = attributes.get(uuid);
        return name == null ? "Not found" : name;
    }
}