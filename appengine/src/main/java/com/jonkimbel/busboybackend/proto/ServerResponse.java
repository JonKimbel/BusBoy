// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: server-response.proto

package com.jonkimbel.busboybackend.proto;

public final class ServerResponse {
  private ServerResponse() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_busboy_api_BusBoyResponse_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_busboy_api_BusBoyResponse_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\025server-response.proto\022\nbusboy.api\"\036\n\016B" +
      "usBoyResponse\022\014\n\004time\030\001 \001(\004B%\n!com.jonki" +
      "mbel.busboybackend.protoP\001"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
        new com.google.protobuf.Descriptors.FileDescriptor.    InternalDescriptorAssigner() {
          public com.google.protobuf.ExtensionRegistry assignDescriptors(
              com.google.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
    internal_static_busboy_api_BusBoyResponse_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_busboy_api_BusBoyResponse_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_busboy_api_BusBoyResponse_descriptor,
        new java.lang.String[] { "Time", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
