/* Automatically generated nanopb constant definitions */
/* Generated by nanopb-0.3.9.2 at Thu Jan 17 20:30:56 2019. */

#include "bus-boy.pb.h"

/* @@protoc_insertion_point(includes) */
#if PB_PROTO_HEADER_VERSION != 30
#error Regenerate this file with the current version of nanopb generator.
#endif



const pb_field_t busboy_api_Response_fields[6] = {
    PB_FIELD(  1, MESSAGE , OPTIONAL, STATIC  , FIRST, busboy_api_Response, time, time, &busboy_api_DisplayedTime_fields),
    PB_FIELD(  2, MESSAGE , REPEATED, CALLBACK, OTHER, busboy_api_Response, arrivals, time, &busboy_api_Arrival_fields),
    PB_FIELD(  3, MESSAGE , REPEATED, CALLBACK, OTHER, busboy_api_Response, routes, arrivals, &busboy_api_Route_fields),
    PB_FIELD(  4, MESSAGE , REPEATED, CALLBACK, OTHER, busboy_api_Response, temporary_message, routes, &busboy_api_TemporaryMessage_fields),
    PB_FIELD(  5, MESSAGE , REPEATED, CALLBACK, OTHER, busboy_api_Response, temporary_style, temporary_message, &busboy_api_TemporaryStyle_fields),
    PB_LAST_FIELD
};

const pb_field_t busboy_api_DisplayedTime_fields[3] = {
    PB_FIELD(  1, UINT64  , OPTIONAL, STATIC  , FIRST, busboy_api_DisplayedTime, ms_since_epoch, ms_since_epoch, 0),
    PB_FIELD(  2, BOOL    , OPTIONAL, STATIC  , OTHER, busboy_api_DisplayedTime, daylight_savings_time, ms_since_epoch, 0),
    PB_LAST_FIELD
};

const pb_field_t busboy_api_Arrival_fields[4] = {
    PB_FIELD(  1, UINT32  , OPTIONAL, STATIC  , FIRST, busboy_api_Arrival, route_index, route_index, 0),
    PB_FIELD(  2, UINT32  , OPTIONAL, STATIC  , OTHER, busboy_api_Arrival, ms_to_arrival, route_index, 0),
    PB_FIELD(  3, BOOL    , OPTIONAL, STATIC  , OTHER, busboy_api_Arrival, predicted, ms_to_arrival, 0),
    PB_LAST_FIELD
};

const pb_field_t busboy_api_Route_fields[3] = {
    PB_FIELD(  1, STRING  , OPTIONAL, CALLBACK, FIRST, busboy_api_Route, short_name, short_name, 0),
    PB_FIELD(  2, STRING  , OPTIONAL, CALLBACK, OTHER, busboy_api_Route, headsign, short_name, 0),
    PB_LAST_FIELD
};

const pb_field_t busboy_api_TemporaryMessage_fields[4] = {
    PB_FIELD(  1, MESSAGE , OPTIONAL, STATIC  , FIRST, busboy_api_TemporaryMessage, time_frame, time_frame, &busboy_api_TimeFrame_fields),
    PB_FIELD(  2, STRING  , REPEATED, CALLBACK, OTHER, busboy_api_TemporaryMessage, message, time_frame, 0),
    PB_FIELD(  3, UENUM   , OPTIONAL, STATIC  , OTHER, busboy_api_TemporaryMessage, color_scheme_override, message, 0),
    PB_LAST_FIELD
};

const pb_field_t busboy_api_TemporaryStyle_fields[3] = {
    PB_FIELD(  1, MESSAGE , OPTIONAL, STATIC  , FIRST, busboy_api_TemporaryStyle, time_frame, time_frame, &busboy_api_TimeFrame_fields),
    PB_FIELD(  2, UENUM   , OPTIONAL, STATIC  , OTHER, busboy_api_TemporaryStyle, color_scheme_override, time_frame, 0),
    PB_LAST_FIELD
};

const pb_field_t busboy_api_TimeFrame_fields[3] = {
    PB_FIELD(  1, UINT64  , OPTIONAL, STATIC  , FIRST, busboy_api_TimeFrame, start_time_ms_since_epoch, start_time_ms_since_epoch, 0),
    PB_FIELD(  2, UINT64  , OPTIONAL, STATIC  , OTHER, busboy_api_TimeFrame, end_time_ms_since_epoch, start_time_ms_since_epoch, 0),
    PB_LAST_FIELD
};



/* Check that field information fits in pb_field_t */
#if !defined(PB_FIELD_32BIT)
/* If you get an error here, it means that you need to define PB_FIELD_32BIT
 * compile-time option. You can do that in pb.h or on compiler command line.
 * 
 * The reason you need to do this is that some of your messages contain tag
 * numbers or field sizes that are larger than what can fit in 8 or 16 bit
 * field descriptors.
 */
PB_STATIC_ASSERT((pb_membersize(busboy_api_Response, time) < 65536 && pb_membersize(busboy_api_TemporaryMessage, time_frame) < 65536 && pb_membersize(busboy_api_TemporaryStyle, time_frame) < 65536), YOU_MUST_DEFINE_PB_FIELD_32BIT_FOR_MESSAGES_busboy_api_Response_busboy_api_DisplayedTime_busboy_api_Arrival_busboy_api_Route_busboy_api_TemporaryMessage_busboy_api_TemporaryStyle_busboy_api_TimeFrame)
#endif

#if !defined(PB_FIELD_16BIT) && !defined(PB_FIELD_32BIT)
/* If you get an error here, it means that you need to define PB_FIELD_16BIT
 * compile-time option. You can do that in pb.h or on compiler command line.
 * 
 * The reason you need to do this is that some of your messages contain tag
 * numbers or field sizes that are larger than what can fit in the default
 * 8 bit descriptors.
 */
PB_STATIC_ASSERT((pb_membersize(busboy_api_Response, time) < 256 && pb_membersize(busboy_api_TemporaryMessage, time_frame) < 256 && pb_membersize(busboy_api_TemporaryStyle, time_frame) < 256), YOU_MUST_DEFINE_PB_FIELD_16BIT_FOR_MESSAGES_busboy_api_Response_busboy_api_DisplayedTime_busboy_api_Arrival_busboy_api_Route_busboy_api_TemporaryMessage_busboy_api_TemporaryStyle_busboy_api_TimeFrame)
#endif


/* @@protoc_insertion_point(eof) */
