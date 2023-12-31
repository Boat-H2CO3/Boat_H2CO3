LOCAL_PATH := $(call my-dir)
HERE_PATH := $(LOCAL_PATH)

# include $(HERE_PATH)/crash_dump/libbase/Android.mk
# include $(HERE_PATH)/crash_dump/libbacktrace/Android.mk
# include $(HERE_PATH)/crash_dump/debuggerd/Android.mk


LOCAL_PATH := $(HERE_PATH)


include $(CLEAR_VARS)
LOCAL_MODULE            := xhook
LOCAL_SRC_FILES         := xhook/xhook.c \
                           xhook/xh_core.c \
                           xhook/xh_elf.c \
                           xhook/xh_jni.c \
                           xhook/xh_log.c \
                           xhook/xh_util.c \
                           xhook/xh_version.c
LOCAL_C_INCLUDES        := $(LOCAL_PATH)/xhook
LOCAL_CFLAGS            := -Wall -Wextra -Werror -fvisibility=hidden
LOCAL_LDLIBS            := -llog
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE     := h2co3_launcher
LOCAL_SRC_FILES  := xhook/xh_core.c \
                    xhook/xh_elf.c \
                    xhook/xh_jni.c \
                    xhook/xh_log.c \
                    xhook/xh_util.c \
                    xhook/xh_version.c \
                    xhook/xhook.c\
                    h2co3_launcher/h2co3_launcher_loader.c \
                    h2co3_launcher/h2co3_launcher.c \
                    h2co3_launcher/h2co3_launcher_clipboard.c \
                    h2co3_launcher/h2co3_launcher_event.c
LOCAL_C_INCLUDES := $(LOCAL_PATH)/h2co3_launcher/include \
                    $(LOCAL_PATH)/xhook
LOCAL_CFLAGS     := -Wall -Wall -Werror
LOCAL_LDLIBS     := -llog -ldl -landroid
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := istdio
LOCAL_SHARED_LIBRARIES := xhook
LOCAL_SRC_FILES := \
    stdio_is.c
LOCAL_C_INCLUDES := $(LOCAL_PATH)/xhook/include
include $(BUILD_SHARED_LIBRARY)

#ifeq ($(TARGET_ARCH_ABI), arm64-v8a)
include $(CLEAR_VARS)
LOCAL_MODULE            := linkerhook
LOCAL_SRC_FILES         := driver_helper/hook.c
LOCAL_LDFLAGS           := -z global
include $(BUILD_SHARED_LIBRARY)
#endif

include $(CLEAR_VARS)
LOCAL_MODULE            := glfw
LOCAL_SHARED_LIBRARIES  := h2co3_launcher
LOCAL_SRC_FILES         := glfw/context.c \
                           glfw/init.c \
                           glfw/input.c \
                           glfw/monitor.c \
                           glfw/vulkan.c \
                           glfw/window.c \
                           glfw/h2co3_launcher_init.c \
                           glfw/h2co3_launcher_monitor.c \
                           glfw/h2co3_launcher_window.c \
                           glfw/egl_context.c \
                           glfw/osmesa_context.c \
                           glfw/posix_thread.c \
                           glfw/posix_time.c \
                           glfw/driver_helper.c \
                           driver_helper/nsbypass.c
LOCAL_C_INCLUDES        := $(LOCAL_PATH)/h2co3_launcher/include \
                           $(LOCAL_PATH)/glfw/include
LOCAL_CFLAGS            := -Wall
LOCAL_LDLIBS            := -llog -ldl -landroid
ifeq ($(TARGET_ARCH_ABI), arm64-v8a)
LOCAL_CFLAGS            += -DADRENO_POSSIBLE
LOCAL_LDLIBS            += -lEGL -lGLESv2
endif
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE            := dyncall
LOCAL_SRC_FILES         := lwjgl/dyncall/$(TARGET_ARCH_ABI)/libdyncall_s.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE            := dyncallback
LOCAL_SRC_FILES         := lwjgl/dyncall/$(TARGET_ARCH_ABI)/libdyncallback_s.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE            := dynload
LOCAL_SRC_FILES         := lwjgl/dyncall/$(TARGET_ARCH_ABI)/libdynload_s.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE            := angle_gles2
LOCAL_SRC_FILES         := tinywrapper/angle-gles/$(TARGET_ARCH_ABI)/libGLESv2_angle.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE            := tinywrapper
LOCAL_SHARED_LIBRARIES  := angle_gles2
LOCAL_SRC_FILES         := tinywrapper/main.c \
                           tinywrapper/string_utils.c
LOCAL_C_INCLUDES        := $(LOCAL_PATH)/tinywrapper
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE            := lwjgl
LOCAL_STATIC_LIBRARIES  := dyncall \
						   dyncallback \
						   dynload
LOCAL_SHARED_LIBRARIES  := h2co3_launcher
LOCAL_SRC_FILES         := lwjgl/h2co3_launcher_hook.c \
						   lwjgl/common_tools.c \
                           lwjgl/org_lwjgl_opengl_AMDDebugOutput.c \
                           lwjgl/org_lwjgl_opengl_AMDDrawBuffersBlend.c \
                           lwjgl/org_lwjgl_opengl_AMDFramebufferMultisampleAdvanced.c \
                           lwjgl/org_lwjgl_opengl_AMDInterleavedElements.c \
                           lwjgl/org_lwjgl_opengl_AMDOcclusionQueryEvent.c \
                           lwjgl/org_lwjgl_opengl_AMDPerformanceMonitor.c \
                           lwjgl/org_lwjgl_opengl_AMDSamplePositions.c \
                           lwjgl/org_lwjgl_opengl_AMDSparseTexture.c \
                           lwjgl/org_lwjgl_opengl_AMDStencilOperationExtended.c \
                           lwjgl/org_lwjgl_opengl_AMDVertexShaderTessellator.c \
                           lwjgl/org_lwjgl_opengl_ARBBindlessTexture.c \
                           lwjgl/org_lwjgl_opengl_ARBBufferStorage.c \
                           lwjgl/org_lwjgl_opengl_ARBClearBufferObject.c \
                           lwjgl/org_lwjgl_opengl_ARBColorBufferFloat.c \
                           lwjgl/org_lwjgl_opengl_ARBComputeVariableGroupSize.c \
                           lwjgl/org_lwjgl_opengl_ARBDebugOutput.c \
                           lwjgl/org_lwjgl_opengl_ARBDrawBuffers.c \
                           lwjgl/org_lwjgl_opengl_ARBDrawBuffersBlend.c \
                           lwjgl/org_lwjgl_opengl_ARBDrawInstanced.c \
                           lwjgl/org_lwjgl_opengl_ARBES32Compatibility.c \
                           lwjgl/org_lwjgl_opengl_ARBFramebufferNoAttachments.c \
                           lwjgl/org_lwjgl_opengl_ARBGeometryShader4.c \
                           lwjgl/org_lwjgl_opengl_ARBGLSPIRV.c \
                           lwjgl/org_lwjgl_opengl_ARBGPUShaderFP64.c \
                           lwjgl/org_lwjgl_opengl_ARBGPUShaderInt64.c \
                           lwjgl/org_lwjgl_opengl_ARBImaging.c \
                           lwjgl/org_lwjgl_opengl_ARBIndirectParameters.c \
                           lwjgl/org_lwjgl_opengl_ARBInstancedArrays.c \
                           lwjgl/org_lwjgl_opengl_ARBMatrixPalette.c \
                           lwjgl/org_lwjgl_opengl_ARBMultisample.c \
                           lwjgl/org_lwjgl_opengl_ARBMultitexture.c \
                           lwjgl/org_lwjgl_opengl_ARBOcclusionQuery.c \
                           lwjgl/org_lwjgl_opengl_ARBParallelShaderCompile.c \
                           lwjgl/org_lwjgl_opengl_ARBPointParameters.c \
                           lwjgl/org_lwjgl_opengl_ARBRobustness.c \
                           lwjgl/org_lwjgl_opengl_ARBSampleLocations.c \
                           lwjgl/org_lwjgl_opengl_ARBSampleShading.c \
                           lwjgl/org_lwjgl_opengl_ARBShaderObjects.c \
                           lwjgl/org_lwjgl_opengl_ARBShadingLanguageInclude.c \
                           lwjgl/org_lwjgl_opengl_ARBSparseBuffer.c \
                           lwjgl/org_lwjgl_opengl_ARBSparseTexture.c \
                           lwjgl/org_lwjgl_opengl_ARBTextureBufferObject.c \
                           lwjgl/org_lwjgl_opengl_ARBTextureBufferRange.c \
                           lwjgl/org_lwjgl_opengl_ARBTextureCompression.c \
                           lwjgl/org_lwjgl_opengl_ARBTextureStorage.c \
                           lwjgl/org_lwjgl_opengl_ARBTextureStorageMultisample.c \
                           lwjgl/org_lwjgl_opengl_ARBTransposeMatrix.c \
                           lwjgl/org_lwjgl_opengl_ARBVertexAttrib64Bit.c \
                           lwjgl/org_lwjgl_opengl_ARBVertexAttribBinding.c \
                           lwjgl/org_lwjgl_opengl_ARBVertexBlend.c \
                           lwjgl/org_lwjgl_opengl_ARBVertexBufferObject.c \
                           lwjgl/org_lwjgl_opengl_ARBVertexProgram.c \
                           lwjgl/org_lwjgl_opengl_ARBVertexShader.c \
                           lwjgl/org_lwjgl_opengl_ARBWindowPos.c \
                           lwjgl/org_lwjgl_opengl_EXTBindableUniform.c \
                           lwjgl/org_lwjgl_opengl_EXTBlendColor.c \
                           lwjgl/org_lwjgl_opengl_EXTBlendEquationSeparate.c \
                           lwjgl/org_lwjgl_opengl_EXTBlendFuncSeparate.c \
                           lwjgl/org_lwjgl_opengl_EXTBlendMinmax.c \
                           lwjgl/org_lwjgl_opengl_EXTCompiledVertexArray.c \
                           lwjgl/org_lwjgl_opengl_EXTDebugLabel.c \
                           lwjgl/org_lwjgl_opengl_EXTDebugMarker.c \
                           lwjgl/org_lwjgl_opengl_EXTDepthBoundsTest.c \
                           lwjgl/org_lwjgl_opengl_EXTDirectStateAccess.c \
                           lwjgl/org_lwjgl_opengl_EXTDrawBuffers2.c \
                           lwjgl/org_lwjgl_opengl_EXTDrawInstanced.c \
                           lwjgl/org_lwjgl_opengl_EXTEGLImageStorage.c \
                           lwjgl/org_lwjgl_opengl_EXTExternalBuffer.c \
                           lwjgl/org_lwjgl_opengl_EXTFramebufferBlit.c \
                           lwjgl/org_lwjgl_opengl_EXTFramebufferMultisample.c \
                           lwjgl/org_lwjgl_opengl_EXTFramebufferObject.c \
                           lwjgl/org_lwjgl_opengl_EXTGeometryShader4.c \
                           lwjgl/org_lwjgl_opengl_EXTGPUProgramParameters.c \
                           lwjgl/org_lwjgl_opengl_EXTGPUShader4.c \
                           lwjgl/org_lwjgl_opengl_EXTMemoryObject.c \
                           lwjgl/org_lwjgl_opengl_EXTMemoryObjectFD.c \
                           lwjgl/org_lwjgl_opengl_EXTMemoryObjectWin32.c \
                           lwjgl/org_lwjgl_opengl_EXTPointParameters.c \
                           lwjgl/org_lwjgl_opengl_EXTPolygonOffsetClamp.c \
                           lwjgl/org_lwjgl_opengl_EXTProvokingVertex.c \
                           lwjgl/org_lwjgl_opengl_EXTRasterMultisample.c \
                           lwjgl/org_lwjgl_opengl_EXTSecondaryColor.c \
                           lwjgl/org_lwjgl_opengl_EXTSemaphore.c \
                           lwjgl/org_lwjgl_opengl_EXTSemaphoreFD.c \
                           lwjgl/org_lwjgl_opengl_EXTSemaphoreWin32.c \
                           lwjgl/org_lwjgl_opengl_EXTSeparateShaderObjects.c \
                           lwjgl/org_lwjgl_opengl_EXTShaderFramebufferFetchNonCoherent.c \
                           lwjgl/org_lwjgl_opengl_EXTShaderImageLoadStore.c \
                           lwjgl/org_lwjgl_opengl_EXTStencilClearTag.c \
                           lwjgl/org_lwjgl_opengl_EXTStencilTwoSide.c \
                           lwjgl/org_lwjgl_opengl_EXTTextureArray.c \
                           lwjgl/org_lwjgl_opengl_EXTTextureBufferObject.c \
                           lwjgl/org_lwjgl_opengl_EXTTextureInteger.c \
                           lwjgl/org_lwjgl_opengl_EXTTimerQuery.c \
                           lwjgl/org_lwjgl_opengl_EXTTransformFeedback.c \
                           lwjgl/org_lwjgl_opengl_EXTVertexAttrib64bit.c \
                           lwjgl/org_lwjgl_opengl_EXTWin32KeyedMutex.c \
                           lwjgl/org_lwjgl_opengl_EXTWindowRectangles.c \
                           lwjgl/org_lwjgl_opengl_EXTX11SyncObject.c \
                           lwjgl/org_lwjgl_opengl_GL11.c \
                           lwjgl/org_lwjgl_opengl_GL11C.c \
                           lwjgl/org_lwjgl_opengl_GL12C.c \
                           lwjgl/org_lwjgl_opengl_GL13.c \
                           lwjgl/org_lwjgl_opengl_GL13C.c \
                           lwjgl/org_lwjgl_opengl_GL14.c \
                           lwjgl/org_lwjgl_opengl_GL14C.c \
                           lwjgl/org_lwjgl_opengl_GL15C.c \
                           lwjgl/org_lwjgl_opengl_GL20C.c \
                           lwjgl/org_lwjgl_opengl_GL21C.c \
                           lwjgl/org_lwjgl_opengl_GL30C.c \
                           lwjgl/org_lwjgl_opengl_GL31C.c \
                           lwjgl/org_lwjgl_opengl_GL32C.c \
                           lwjgl/org_lwjgl_opengl_GL33.c \
                           lwjgl/org_lwjgl_opengl_GL33C.c \
                           lwjgl/org_lwjgl_opengl_GL40C.c \
                           lwjgl/org_lwjgl_opengl_GL41C.c \
                           lwjgl/org_lwjgl_opengl_GL42C.c \
                           lwjgl/org_lwjgl_opengl_GL43C.c \
                           lwjgl/org_lwjgl_opengl_GL44C.c \
                           lwjgl/org_lwjgl_opengl_GL45.c \
                           lwjgl/org_lwjgl_opengl_GL45C.c \
                           lwjgl/org_lwjgl_opengl_GL46C.c \
                           lwjgl/org_lwjgl_opengl_GREMEDYFrameTerminator.c \
                           lwjgl/org_lwjgl_opengl_GREMEDYStringMarker.c \
                           lwjgl/org_lwjgl_opengl_INTELFramebufferCMAA.c \
                           lwjgl/org_lwjgl_opengl_INTELMapTexture.c \
                           lwjgl/org_lwjgl_opengl_INTELPerformanceQuery.c \
                           lwjgl/org_lwjgl_opengl_KHRBlendEquationAdvanced.c \
                           lwjgl/org_lwjgl_opengl_KHRParallelShaderCompile.c \
                           lwjgl/org_lwjgl_opengl_NVAlphaToCoverageDitherControl.c \
                           lwjgl/org_lwjgl_opengl_NVBindlessMultiDrawIndirect.c \
                           lwjgl/org_lwjgl_opengl_NVBindlessMultiDrawIndirectCount.c \
                           lwjgl/org_lwjgl_opengl_NVBindlessTexture.c \
                           lwjgl/org_lwjgl_opengl_NVBlendEquationAdvanced.c \
                           lwjgl/org_lwjgl_opengl_NVClipSpaceWScaling.c \
                           lwjgl/org_lwjgl_opengl_NVCommandList.c \
                           lwjgl/org_lwjgl_opengl_NVConditionalRender.c \
                           lwjgl/org_lwjgl_opengl_NVConservativeRaster.c \
                           lwjgl/org_lwjgl_opengl_NVConservativeRasterDilate.c \
                           lwjgl/org_lwjgl_opengl_NVConservativeRasterPreSnapTriangles.c \
                           lwjgl/org_lwjgl_opengl_NVCopyImage.c \
                           lwjgl/org_lwjgl_opengl_NVDepthBufferFloat.c \
                           lwjgl/org_lwjgl_opengl_NVDrawTexture.c \
                           lwjgl/org_lwjgl_opengl_NVDrawVulkanImage.c \
                           lwjgl/org_lwjgl_opengl_NVExplicitMultisample.c \
                           lwjgl/org_lwjgl_opengl_NVFence.c \
                           lwjgl/org_lwjgl_opengl_NVFragmentCoverageToColor.c \
                           lwjgl/org_lwjgl_opengl_NVFramebufferMixedSamples.c \
                           lwjgl/org_lwjgl_opengl_NVFramebufferMultisampleCoverage.c \
                           lwjgl/org_lwjgl_opengl_NVGPUMulticast.c \
                           lwjgl/org_lwjgl_opengl_NVGPUShader5.c \
                           lwjgl/org_lwjgl_opengl_NVHalfFloat.c \
                           lwjgl/org_lwjgl_opengl_NVInternalformatSampleQuery.c \
                           lwjgl/org_lwjgl_opengl_NVMemoryAttachment.c \
                           lwjgl/org_lwjgl_opengl_NVMeshShader.c \
                           lwjgl/org_lwjgl_opengl_NVPathRendering.c \
                           lwjgl/org_lwjgl_opengl_NVPixelDataRange.c \
                           lwjgl/org_lwjgl_opengl_NVPointSprite.c \
                           lwjgl/org_lwjgl_opengl_NVPrimitiveRestart.c \
                           lwjgl/org_lwjgl_opengl_NVQueryResource.c \
                           lwjgl/org_lwjgl_opengl_NVQueryResourceTag.c \
                           lwjgl/org_lwjgl_opengl_NVSampleLocations.c \
                           lwjgl/org_lwjgl_opengl_NVScissorExclusive.c \
                           lwjgl/org_lwjgl_opengl_NVShaderBufferLoad.c \
                           lwjgl/org_lwjgl_opengl_NVShadingRateImage.c \
                           lwjgl/org_lwjgl_opengl_NVTextureBarrier.c \
                           lwjgl/org_lwjgl_opengl_NVTextureMultisample.c \
                           lwjgl/org_lwjgl_opengl_NVTransformFeedback.c \
                           lwjgl/org_lwjgl_opengl_NVTransformFeedback2.c \
                           lwjgl/org_lwjgl_opengl_NVVertexArrayRange.c \
                           lwjgl/org_lwjgl_opengl_NVVertexAttribInteger64bit.c \
                           lwjgl/org_lwjgl_opengl_NVVertexBufferUnifiedMemory.c \
                           lwjgl/org_lwjgl_opengl_NVViewportSwizzle.c \
                           lwjgl/org_lwjgl_opengl_NVXConditionalRender.c \
                           lwjgl/org_lwjgl_opengl_NVXGpuMulticast2.c \
                           lwjgl/org_lwjgl_opengl_NVXProgressFence.c \
                           lwjgl/org_lwjgl_opengl_OVRMultiview.c \
                           lwjgl/org_lwjgl_stb_LibSTB.c \
                           lwjgl/org_lwjgl_stb_STBDXT.c \
                           lwjgl/org_lwjgl_stb_STBEasyFont.c \
                           lwjgl/org_lwjgl_stb_STBImage.c \
                           lwjgl/org_lwjgl_stb_STBImageResize.c \
                           lwjgl/org_lwjgl_stb_STBImageWrite.c \
                           lwjgl/org_lwjgl_stb_STBPerlin.c \
                           lwjgl/org_lwjgl_stb_STBRectPack.c \
                           lwjgl/org_lwjgl_stb_STBTruetype.c \
                           lwjgl/org_lwjgl_stb_STBTTFontinfo.c \
                           lwjgl/org_lwjgl_stb_STBVorbis.c \
                           lwjgl/org_lwjgl_system_Callback.c \
                           lwjgl/org_lwjgl_system_dyncall_DynCall.c \
                           lwjgl/org_lwjgl_system_dyncall_DynCallback.c \
                           lwjgl/org_lwjgl_system_dyncall_DynLoad.c \
                           lwjgl/org_lwjgl_system_h2co3_launcher_DynamicLinkLoader.c \
                           lwjgl/org_lwjgl_system_JNI.c \
                           lwjgl/org_lwjgl_system_jni_JNINativeInterface.c \
                           lwjgl/org_lwjgl_system_libc_LibCErrno.c \
                           lwjgl/org_lwjgl_system_libc_LibCLocale.c \
                           lwjgl/org_lwjgl_system_libc_LibCStdio.c \
                           lwjgl/org_lwjgl_system_libc_LibCStdlib.c \
                           lwjgl/org_lwjgl_system_libc_LibCString.c \
                           lwjgl/org_lwjgl_system_MemoryAccessJNI.c \
                           lwjgl/org_lwjgl_system_MemoryUtil.c \
                           lwjgl/org_lwjgl_system_ThreadLocalUtil.c \
                           lwjgl/org_lwjgl_util_tinyfd_TinyFileDialogs.c \
                           lwjgl/tinyfiledialogs.c

LOCAL_CFLAGS            := -O2 -Wall -c -fPIC -std=c99 -Wunused -DLWJGL_h2co3_launcher -Wunused-value
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)

LOCAL_MODULE := gl4es

LOCAL_C_INCLUDES := $(LOCAL_PATH)/include

LOCAL_EXPORT_C_INCLUDES := $(LOCAL_C_INCLUDES) -DBCMHOST

LOCAL_SRC_FILES := \
	holy_gl114/src/gl/arbconverter.c \
	holy_gl114/src/gl/arbgenerator.c \
	holy_gl114/src/gl/arbhelper.c \
	holy_gl114/src/gl/arbparser.c \
	holy_gl114/src/gl/array.c \
	holy_gl114/src/gl/blend.c \
	holy_gl114/src/gl/blit.c \
	holy_gl114/src/gl/buffers.c \
	holy_gl114/src/gl/build_info.c \
	holy_gl114/src/gl/debug.c \
	holy_gl114/src/gl/decompress.c \
	holy_gl114/src/gl/depth.c \
	holy_gl114/src/gl/directstate.c \
	holy_gl114/src/gl/drawing.c \
	holy_gl114/src/gl/enable.c \
	holy_gl114/src/gl/envvars.c \
	holy_gl114/src/gl/eval.c \
	holy_gl114/src/gl/face.c \
	holy_gl114/src/gl/fog.c \
	holy_gl114/src/gl/fpe.c \
	holy_gl114/src/gl/fpe_cache.c \
	holy_gl114/src/gl/fpe_shader.c \
	holy_gl114/src/gl/framebuffers.c \
	holy_gl114/src/gl/gl_lookup.c \
	holy_gl114/src/gl/getter.c \
	holy_gl114/src/gl/gl4es.c \
	holy_gl114/src/gl/glstate.c \
	holy_gl114/src/gl/hint.c \
	holy_gl114/src/gl/init.c \
	holy_gl114/src/gl/light.c \
	holy_gl114/src/gl/line.c \
	holy_gl114/src/gl/list.c \
	holy_gl114/src/gl/listdraw.c \
	holy_gl114/src/gl/listrl.c \
	holy_gl114/src/gl/loader.c \
	holy_gl114/src/gl/logs.c \
	holy_gl114/src/gl/matrix.c \
	holy_gl114/src/gl/matvec.c \
	holy_gl114/src/gl/oldprogram.c \
	holy_gl114/src/gl/pixel.c \
	holy_gl114/src/gl/planes.c \
	holy_gl114/src/gl/pointsprite.c \
	holy_gl114/src/gl/preproc.c \
	holy_gl114/src/gl/program.c \
	holy_gl114/src/gl/queries.c \
	holy_gl114/src/gl/raster.c \
	holy_gl114/src/gl/render.c \
	holy_gl114/src/gl/shader.c \
	holy_gl114/src/gl/shaderconv.c \
	holy_gl114/src/gl/shader_hacks.c \
	holy_gl114/src/gl/stack.c \
	holy_gl114/src/gl/stencil.c \
	holy_gl114/src/gl/string_utils.c \
	holy_gl114/src/gl/stubs.c \
	holy_gl114/src/gl/texenv.c \
	holy_gl114/src/gl/texgen.c \
	holy_gl114/src/gl/texture.c \
	holy_gl114/src/gl/texture_compressed.c \
	holy_gl114/src/gl/texture_params.c \
	holy_gl114/src/gl/texture_read.c \
	holy_gl114/src/gl/texture_3d.c \
	holy_gl114/src/gl/uniform.c \
	holy_gl114/src/gl/vertexattrib.c \
	holy_gl114/src/gl/wrap/gl4eswraps.c \
	holy_gl114/src/gl/wrap/gles.c \
	holy_gl114/src/gl/wrap/glstub.c \
	holy_gl114/src/gl/math/matheval.c \
	holy_gl114/src/glx/hardext.c \
	holy_gl114/src/glx/glx.c \
	holy_gl114/src/glx/lookup.c \
	holy_gl114/src/glx/gbm.c \
	holy_gl114/src/glx/streaming.c \
	holy_gl114/src/gl/vgpu/shaderconv.c

LOCAL_CFLAGS            := -O2 -Wall -c -fPIC -std=gnu99 -Wunused -DLWJGL_h2co3_launcher -Wunused-value
LOCAL_CFLAGS += -DNOX11
LOCAL_CFLAGS += -DNO_GBM
#LOCAL_CFLAGS += -DNO_INIT_CONSTRUCTOR
LOCAL_CFLAGS += -DDEFAULT_ES=2

LOCAL_LDLIBS := -ldl -llog
#building as a static lib

include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)

LOCAL_MODULE := gl4es_egl

LOCAL_C_INCLUDES := $(LOCAL_PATH)/holy_gl114/include

LOCAL_EXPORT_C_INCLUDES := $(LOCAL_C_INCLUDES) -DBCMHOST

LOCAL_SRC_FILES := \
	holy_gl114/src/egl/egl.c \
                holy_gl114/src/egl/lookup.c

LOCAL_CFLAGS += -g -std=gnu99 -funwind-tables -O3 -fvisibility=hidden

LOCAL_SHARED_LIBRARIES := gl4es

LOCAL_LDLIBS := -ldl

include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := h2co3_exec_awt
LOCAL_SHARED_LIBRARIES  := h2co3_launcher
LOCAL_SRC_FILES := awt/awt_bridge.c
LOCAL_C_INCLUDES := $(LOCAL_PATH)/h2co3_launcher/include
include $(BUILD_SHARED_LIBRARY)

# Helper to get current thread
# include $(CLEAR_VARS)
# LOCAL_MODULE := thread64helper
# LOCAL_SRC_FILES := thread_helper.cpp
# include $(BUILD_SHARED_LIBRARY)

# fake lib for linker
include $(CLEAR_VARS)
LOCAL_MODULE := awt_headless
include $(BUILD_SHARED_LIBRARY)

# libawt_xawt without X11, used to get Caciocavallo working
LOCAL_PATH := $(HERE_PATH)/awt_xawt
include $(CLEAR_VARS)
LOCAL_MODULE := awt_xawt
# LOCAL_CFLAGS += -DHEADLESS
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)
LOCAL_SHARED_LIBRARIES := awt_headless
LOCAL_SRC_FILES := xawt_fake.c
include $(BUILD_SHARED_LIBRARY)

# delete fake libs after linked
$(info $(shell (rm $(HERE_PATH)/../jniLibs/*/libawt_headless.so)))

