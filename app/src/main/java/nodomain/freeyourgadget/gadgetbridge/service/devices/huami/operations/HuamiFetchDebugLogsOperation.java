/*  Copyright (C) 2017-2021 Andreas Shimokawa, Carsten Pfeiffer, Daniele
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
package nodomain.freeyourgadget.gadgetbridge.service.devices.huami.operations;

import android.widget.Toast;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import androidx.annotation.NonNull;

import nodomain.freeyourgadget.gadgetbridge.R;
import nodomain.freeyourgadget.gadgetbridge.devices.huami.amazfitbip.AmazfitBipService;
import nodomain.freeyourgadget.gadgetbridge.service.btle.BLETypeConversions;
import nodomain.freeyourgadget.gadgetbridge.service.btle.TransactionBuilder;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.HuamiSupport;
import nodomain.freeyourgadget.gadgetbridge.util.FileUtils;
import nodomain.freeyourgadget.gadgetbridge.util.GB;

public class HuamiFetchDebugLogsOperation extends AbstractFetchOperation {
    private static final Logger LOG = LoggerFactory.getLogger(HuamiFetchDebugLogsOperation.class);

    private FileOutputStream logOutputStream;

    public HuamiFetchDebugLogsOperation(HuamiSupport support) {
        super(support);
        setName("fetch debug logs");
    }

    @Override
    protected String taskDescription() {
        return getContext().getString(R.string.busy_task_fetch_debug_logs);
    }

    @Override
    protected void startFetching(TransactionBuilder builder) {
        File dir;
        try {
            dir = FileUtils.getExternalFilesDir();
        } catch (IOException e) {
            return;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US);
        String filename = "huamidebug_" + dateFormat.format(new Date()) + ".log";

        File outputFile = new File(dir, filename );
        try {
            logOutputStream = new FileOutputStream(outputFile);
        } catch (IOException e) {
            LOG.warn("could not create file " + outputFile, e);
            return;
        }

        GregorianCalendar sinceWhen = BLETypeConversions.createCalendar();
        sinceWhen.add(Calendar.DAY_OF_MONTH, -10);
        startFetching(builder, AmazfitBipService.COMMAND_ACTIVITY_DATA_TYPE_DEBUGLOGS, sinceWhen);
    }

    @Override
    protected String getLastSyncTimeKey() {
        return null;
    }

    @Override
    protected boolean handleActivityFetchFinish(boolean success) {
        LOG.info("{} data has finished", getName());
        try {
            logOutputStream.close();
            logOutputStream = null;
        } catch (IOException e) {
            LOG.warn("could not close output stream", e);
            return false;
        }

        return super.handleActivityFetchFinish(success);
    }

    @Override
    protected boolean validChecksum(int crc32) {
        // TODO actually check it?
        LOG.warn("Checksum not implemented for debug logs, assuming it's valid");
        return true;
    }

    @Override
    protected void handleActivityNotif(byte[] value) {
        if (!isOperationRunning()) {
            LOG.error("ignoring notification because operation is not running. Data length: " + value.length);
            getSupport().logMessageContent(value);
            return;
        }

        if ((byte) (lastPacketCounter + 1) == value[0]) {
            lastPacketCounter++;
            bufferActivityData(value);
        } else {
            GB.toast("Error " + getName() + " invalid package counter: " + value[0], Toast.LENGTH_LONG, GB.ERROR);
            handleActivityFetchFinish(false);
        }
    }

    @Override
    protected void bufferActivityData(@NonNull byte[] value) {
        try {
            logOutputStream.write(value, 1, value.length - 1);
        } catch (final IOException e) {
            LOG.warn("could not write to output stream", e);
            handleActivityFetchFinish(false);
        }
    }
}
