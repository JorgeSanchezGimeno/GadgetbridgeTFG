/*  Copyright (C) 2022 José Rebelo

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
package nodomain.freeyourgadget.gadgetbridge.devices.huami.amazfittrex2;

import android.content.Context;
import android.net.Uri;

import java.io.IOException;

import nodomain.freeyourgadget.gadgetbridge.devices.huami.HuamiFWHelper;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.amazfittrex2.AmazfitTRex2FirmwareInfo;

public class AmazfitTRex2FWHelper extends HuamiFWHelper {
    public AmazfitTRex2FWHelper(final Uri uri, final Context context) throws IOException {
        super(uri, context);
    }

    @Override
    public long getMaxExpectedFileSize() {
        return 1024 * 1024 * 128; // 128.0MB
    }

    @Override
    protected void determineFirmwareInfo(final byte[] wholeFirmwareBytes) {
        firmwareInfo = new AmazfitTRex2FirmwareInfo(wholeFirmwareBytes);
        if (!firmwareInfo.isHeaderValid()) {
            throw new IllegalArgumentException("Not a Amazfit T-Rex 2 firmware");
        }
    }
}
