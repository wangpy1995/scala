package hadoop.mapreduce.input;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.LineRecordReader;
import org.apache.parquet.bytes.BytesUtils;

import java.io.IOException;

/**
 * Created by wpy on 2017/6/15.
 */
public class MyKeyValueRecordReader extends RecordReader<LongWritable, Text> {
    public static final String KEY_VALUE_SEPERATOR =
            "mapreduce.input.keyvaluelinerecordreader.key.value.separator";

    private final LineRecordReader lineRecordReader;

    private byte separator = (byte) ' ';

    private Text innerValue;

    private static Text k = new Text();
    private LongWritable key;

    private Text value;

    public Class getKeyClass() {
        return Text.class;
    }

    public MyKeyValueRecordReader(Configuration conf)
            throws IOException {

        lineRecordReader = new LineRecordReader();
        String sepStr = conf.get(KEY_VALUE_SEPERATOR, " ");
        this.separator = (byte) sepStr.charAt(0);
    }

    public void initialize(InputSplit genericSplit,
                           TaskAttemptContext context) throws IOException {
        lineRecordReader.initialize(genericSplit, context);
    }

    public static int findSeparator(byte[] utf, int start, int length,
                                    byte sep) {
        for (int i = start; i < (start + length); i++) {
            if (utf[i] == sep) {
                return i;
            }
        }
        return -1;
    }

    public static void setKeyValue(LongWritable key, Text value, byte[] line,
                                   int lineLen, int pos) {
        if (pos == -1) {
            k.set(line, 0, lineLen);
            key.set(BytesUtils.bytesToLong(k.getBytes()));
            value.set("");
        } else {
            k.set(line, 0, pos);
            key.set(BytesUtils.bytesToLong(k.getBytes()));
            value.set(line, pos + 1, lineLen - pos - 1);
        }
    }

    /**
     * Read key/value pair in a line.
     */
    public synchronized boolean nextKeyValue()
            throws IOException {
        byte[] line = null;
        int lineLen = -1;
        if (lineRecordReader.nextKeyValue()) {
            innerValue = lineRecordReader.getCurrentValue();
            line = innerValue.getBytes();
            lineLen = innerValue.getLength();
        } else {
            return false;
        }
        if (line == null)
            return false;
        if (key == null) {
            key = new LongWritable();
        }
        if (value == null) {
            value = new Text();
        }
        int pos = findSeparator(line, 0, lineLen, this.separator);
        setKeyValue(key, value, line, lineLen, pos);
        return true;
    }

    public LongWritable getCurrentKey() {
        return key;
    }

    public Text getCurrentValue() {
        return value;
    }

    public float getProgress() throws IOException {
        return lineRecordReader.getProgress();
    }

    public synchronized void close() throws IOException {
        lineRecordReader.close();
    }
}