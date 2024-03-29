/*  Copyright (C) 2016-2021 Andreas Shimokawa, Carsten Pfeiffer, Daniele
    Gobbetti

    This file is part of Gadgetbridge.

    Gadgetbridge is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Gadgetbridge is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>. */
package nodomain.freeyourgadget.gadgetbridge.devices.huami;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import java.io.IOException;

import androidx.annotation.NonNull;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.R;
import nodomain.freeyourgadget.gadgetbridge.devices.miband.AbstractMiBandFWHelper;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.AbstractHuamiFirmwareInfo;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.HuamiFirmwareType;

public abstract class HuamiFWHelper extends AbstractMiBandFWHelper {
    protected AbstractHuamiFirmwareInfo firmwareInfo;

    public HuamiFWHelper(Uri uri, Context context) throws IOException {
        super(uri, context);
    }

    @Override
    public String format(int version) {
        return firmwareInfo.toVersion(version);
    }

    @NonNull
    @Override
    public String getFirmwareKind() {
        int resId = R.string.kind_invalid;
        switch (getFirmwareInfo().getFirmwareType()) {
            case FONT:
            case FONT_LATIN:
                resId = R.string.kind_font;
                break;
            case GPS:
                resId = R.string.kind_gps;
                break;
            case GPS_ALMANAC:
                resId = R.string.kind_gps_almanac;
                break;
            case GPS_CEP:
                resId = R.string.kind_gps_cep;
                break;
            case AGPS_UIHH:
                resId = R.string.kind_agps_bundle;
                break;
            case RES:
            case RES_COMPRESSED:
                resId = R.string.kind_resources;
                break;
            case FIRMWARE:
            case FIRMWARE_UIHH_2021_ZIP_WITH_CHANGELOG:
                resId = R.string.kind_firmware;
                break;
            case WATCHFACE:
                resId = R.string.kind_watchface;
                break;
            case APP:
                resId = R.string.kind_app;
                break;
            case INVALID:
                // fall through
        }
        return GBApplication.getContext().getString(resId);
    }

    @Override
    public int getFirmwareVersion() {
        return firmwareInfo.getFirmwareVersion();
    }

    @Override
    public int getFirmware2Version() {
        return 0;
    }

    @Override
    public String getHumanFirmwareVersion2() {
        return "";
    }

    @Override
    protected int[] getWhitelistedFirmwareVersions() {
        return firmwareInfo.getWhitelistedVersions();
    }

    @Override
    public boolean isFirmwareGenerallyCompatibleWith(GBDevice device) {
        return firmwareInfo.isGenerallyCompatibleWith(device);
    }

    @Override
    public boolean isSingleFirmware() {
        return true;
    }

    @Override
    public void checkValid() throws IllegalArgumentException {
        firmwareInfo.checkValid();
    }

    @Override
    public HuamiFirmwareType getFirmwareType() {
        return firmwareInfo.getFirmwareType();
    }

    @Override
    public void unsetFwBytes() {
        super.unsetFwBytes();
        firmwareInfo.unsetFwBytes();
    }

    public AbstractHuamiFirmwareInfo getFirmwareInfo() {
        return firmwareInfo;
    }

    @Override
    public Bitmap getPreview() {
        if (firmwareInfo != null) {
            return firmwareInfo.getPreview();
        }

        return null;
    }
}
