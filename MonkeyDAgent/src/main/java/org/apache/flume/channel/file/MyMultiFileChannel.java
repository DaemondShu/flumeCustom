package org.apache.flume.channel.file;

/**
 * Created by monkey_d_asce on 17-5-22.
 */
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.flume.Context;
import org.apache.flume.channel.BasicChannelSemantics;
import org.apache.flume.channel.BasicTransactionSemantics;
import org.apache.flume.lifecycle.LifecycleState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;


public class MyMultiFileChannel extends BasicChannelSemantics {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyMultiFileChannel.class);

    private Context context;

    private int channels;

    private String checkpointDirStr;

    private String dataDirStr;

    private List<MyFileChannel> fileChannels = null;

    /*public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public int getChannels() {
        return channels;
    }

    public void setChannels(int channels) {
        this.channels = channels;
    }

    public String getCheckpointDirStr() {
        return checkpointDirStr;
    }

    public void setCheckpointDirStr(String checkpointDirStr) {
        this.checkpointDirStr = checkpointDirStr;
    }

    public String getDataDirStr() {
        return dataDirStr;
    }

    public void setDataDirStr(String dataDirStr) {
        this.dataDirStr = dataDirStr;
    }

    public List<MyFileChannel> getFileChannels() {
        return fileChannels;
    }

    public void setFileChannels(List<MyFileChannel> fileChannels) {
        this.fileChannels = fileChannels;
    }

    TODO 增加函数自动添加filechannel
    */

    @Override
    public void configure(Context context) {
        this.context = context;

        channels = context.getInteger("channels");
        LOGGER.info("channels: {}", channels);

        Preconditions.checkState(channels > 0, "channels's value must be greater than zero");

        checkpointDirStr = context.getString(FileChannelConfiguration.CHECKPOINT_DIR);
        LOGGER.info("checkpointDir: {}", checkpointDirStr);

        Preconditions.checkState(StringUtils.isNotEmpty(checkpointDirStr),
                "checkpointDirStr's value must not be empty");

        dataDirStr = context.getString(FileChannelConfiguration.DATA_DIRS);
        LOGGER.info("dataDir: {}", dataDirStr);

        Preconditions.checkState(StringUtils.isNotEmpty(dataDirStr), "dataDirStr's value must not be empty");

        LOGGER.info("MultipleFileChannel configure success");
    }

    @Override
    public synchronized void start() {
        File checkpointDir = new File(checkpointDirStr);

        if (!checkpointDir.exists()) {
            checkpointDir.mkdirs();
        }

        File dataDir = new File(dataDirStr);

        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        fileChannels = new ArrayList<>();

        for (int index = 0; index < channels; index++) {
            MyFileChannel fileChannel = new MyFileChannel();

            fileChannel.setName(getName() + "_filechannel_" + index);

            Context ctx = new Context(context.getParameters());

            ctx.put(FileChannelConfiguration.CHECKPOINT_DIR,
                    new File(checkpointDir, String.valueOf(index)).getAbsolutePath());
            ctx.put(FileChannelConfiguration.DATA_DIRS, new File(dataDir, String.valueOf(index)).getAbsolutePath());

            fileChannel.configure(ctx);

            fileChannels.add(fileChannel);
        }

        for (MyFileChannel fileChannel : fileChannels) {
            fileChannel.start();
        }
        super.start();
    }

    @Override
    protected BasicTransactionSemantics createTransaction() {
        try {
            return fileChannels.get((int) (System.currentTimeMillis() % channels)).createTransaction();
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    @Override
    public synchronized void stop() {
        if (getLifecycleState() == LifecycleState.STOP) {
            return;
        }

        for (MyFileChannel fileChannel : fileChannels) {
            fileChannel.stop();
        }
        fileChannels.clear();
        super.stop();
    }

}