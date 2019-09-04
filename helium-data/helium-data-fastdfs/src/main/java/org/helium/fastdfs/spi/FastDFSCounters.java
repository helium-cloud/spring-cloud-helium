package org.helium.fastdfs.spi;

import org.helium.perfmon.PerformanceCounterType;
import org.helium.perfmon.SmartCounter;
import org.helium.perfmon.annotation.PerformanceCounter;
import org.helium.perfmon.annotation.PerformanceCounterCategory;

/**
 * Created by lvmingwei on 16-1-4.
 */
@PerformanceCounterCategory("FastDFS")
public class FastDFSCounters {

    @PerformanceCounter(name = "txUploadFile", type = PerformanceCounterType.TRANSACTION)
    private SmartCounter txUploadFile;

    @PerformanceCounter(name = "txUploadAppenderFile", type = PerformanceCounterType.TRANSACTION)
    private SmartCounter txUploadAppenderFile;

    @PerformanceCounter(name = "txAppendFile", type = PerformanceCounterType.TRANSACTION)
    private SmartCounter txAppendFile;

    @PerformanceCounter(name = "txAppendFileOffset", type = PerformanceCounterType.TRANSACTION)
    private SmartCounter txAppendFileOffset;

    @PerformanceCounter(name = "txModifyFile", type = PerformanceCounterType.TRANSACTION)
    private SmartCounter txModifyFile;

    @PerformanceCounter(name = "txModifyFileOffset", type = PerformanceCounterType.TRANSACTION)
    private SmartCounter txModifyFileOffset;

    @PerformanceCounter(name = "txTruncateFile", type = PerformanceCounterType.TRANSACTION)
    private SmartCounter txTruncateFile;

    @PerformanceCounter(name = "txDeleteFile", type = PerformanceCounterType.TRANSACTION)
    private SmartCounter txDeleteFile;

    @PerformanceCounter(name = "txDownloadFile", type = PerformanceCounterType.TRANSACTION)
    private SmartCounter txDownloadFile;

    @PerformanceCounter(name = "txDownloadFileOffset", type = PerformanceCounterType.TRANSACTION)
    private SmartCounter txDownloadFileOffset;

    @PerformanceCounter(name = "txQueryFileInfo", type = PerformanceCounterType.TRANSACTION)
    private SmartCounter txQueryFileInfo;

    @PerformanceCounter(name = "txGetMetadata", type = PerformanceCounterType.TRANSACTION)
    private SmartCounter txGetMetadata;

    @PerformanceCounter(name = "txSetMetadata", type = PerformanceCounterType.TRANSACTION)
    private SmartCounter txSetMetadata;

    public SmartCounter getTxUploadFile() {
        return txUploadFile;
    }

    public void setTxUploadFile(SmartCounter txUploadFile) {
        this.txUploadFile = txUploadFile;
    }

    public SmartCounter getTxUploadAppenderFile() {
        return txUploadAppenderFile;
    }

    public void setTxUploadAppenderFile(SmartCounter txUploadAppenderFile) {
        this.txUploadAppenderFile = txUploadAppenderFile;
    }

    public SmartCounter getTxAppendFile() {
        return txAppendFile;
    }

    public void setTxAppendFile(SmartCounter txAppendFile) {
        this.txAppendFile = txAppendFile;
    }

    public SmartCounter getTxAppendFileOffset() {
        return txAppendFileOffset;
    }

    public void setTxAppendFileOffset(SmartCounter txAppendFileOffset) {
        this.txAppendFileOffset = txAppendFileOffset;
    }

    public SmartCounter getTxModifyFile() {
        return txModifyFile;
    }

    public void setTxModifyFile(SmartCounter txModifyFile) {
        this.txModifyFile = txModifyFile;
    }

    public SmartCounter getTxModifyFileOffset() {
        return txModifyFileOffset;
    }

    public void setTxModifyFileOffset(SmartCounter txModifyFileOffset) {
        this.txModifyFileOffset = txModifyFileOffset;
    }

    public SmartCounter getTxTruncateFile() {
        return txTruncateFile;
    }

    public void setTxTruncateFile(SmartCounter txTruncateFile) {
        this.txTruncateFile = txTruncateFile;
    }

    public SmartCounter getTxDeleteFile() {
        return txDeleteFile;
    }

    public void setTxDeleteFile(SmartCounter txDeleteFile) {
        this.txDeleteFile = txDeleteFile;
    }

    public SmartCounter getTxDownloadFile() {
        return txDownloadFile;
    }

    public void setTxDownloadFile(SmartCounter txDownloadFile) {
        this.txDownloadFile = txDownloadFile;
    }

    public SmartCounter getTxDownloadFileOffset() {
        return txDownloadFileOffset;
    }

    public void setTxDownloadFileOffset(SmartCounter txDownloadFileOffset) {
        this.txDownloadFileOffset = txDownloadFileOffset;
    }

    public SmartCounter getTxQueryFileInfo() {
        return txQueryFileInfo;
    }

    public void setTxQueryFileInfo(SmartCounter txQueryFileInfo) {
        this.txQueryFileInfo = txQueryFileInfo;
    }

    public SmartCounter getTxGetMetadata() {
        return txGetMetadata;
    }

    public void setTxGetMetadata(SmartCounter txGetMetadata) {
        this.txGetMetadata = txGetMetadata;
    }

    public SmartCounter getTxSetMetadata() {
        return txSetMetadata;
    }

    public void setTxSetMetadata(SmartCounter txSetMetadata) {
        this.txSetMetadata = txSetMetadata;
    }
}
