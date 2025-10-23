SUMMARY = "Chromium EC Firmware"
DEPENDS = "libusb1-native pkgconfig-native libftdi-native"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=562c740877935f40b262db8af30bca36"

SRCREV = "0fa2513fc1c5c3c328e617c1593435a97d109d6f"
SRC_URI = "git://chromium.googlesource.com/chromiumos/platform/ec;protocol=https;branch=master \
           file://0001-Makefile.toolchain-ignore-stringop-overread-warning.patch \
           file://0002-ignore-reprecation-warning-ftdi_usb_purge_buffers-is.patch \
          "
inherit deploy

COMPATIBLE_MACHINE = "discovery-stm32f072|nucleo-f411re"

CROS_EC_BOARD:discovery-stm32f072 ?= "discovery-stm32f072"
CROS_EC_BOARD:nucleo-f411re ?= "nucleo-f411re"

PV = "2.3.9999+gitr${SRCPV}"

S = "${WORKDIR}/git"

EXTRA_OEMAKE = "'CROSS_COMPILE=arm-oe-eabi-' 'PKG_CONFIG=pkg-config-native'"
BUILD_CFLAGS += "-Wno-error=stringop-overread"

do_compile() {
    oe_runmake BOARD=${CROS_EC_BOARD}
}

do_install() {
    :
}

CROS_EC_FIRMWARE_BASE_NAME ?= "${BPN}-${PKGV}-${PKGR}-${MACHINE}${IMAGE_VERSION_SUFFIX}"
CROS_EC_FIRMWARE_BASE_NAME[vardepsexclude] = "IMAGE_VERSION_SUFFIX"

do_deploy() {
    install -m 0644 ${B}/build/${CROS_EC_BOARD}/ec.bin ${DEPLOYDIR}/${CROS_EC_FIRMWARE_BASE_NAME}.bin
    install -m 0644 ${B}/build/${CROS_EC_BOARD}/RW/ec.RW.bin ${DEPLOYDIR}/${CROS_EC_FIRMWARE_BASE_NAME}.RW.bin
    ln -sf ${CROS_EC_FIRMWARE_BASE_NAME}.bin ${DEPLOYDIR}/${BPN}-${MACHINE}.bin
    ln -sf ${CROS_EC_FIRMWARE_BASE_NAME}.RW.bin ${DEPLOYDIR}/${BPN}-${MACHINE}.RW.bin
}
addtask deploy before do_build after do_install
