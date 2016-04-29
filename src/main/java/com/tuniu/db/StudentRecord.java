package com.tuniu.db;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapred.lib.db.DBWritable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by wuzhong on 2016/4/29.
 */
public class StudentRecord implements Writable, DBWritable {

    int id;
    String name;
    String gender;
    String number;

    @Override
    public String toString() {
        return new String(this.name + " " + this.gender + " " + this.number);
    }

    public void write(PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setInt(1, this.id);
        preparedStatement.setString(2, this.name);
        preparedStatement.setString(3, this.gender);
        preparedStatement.setString(4, this.number);
    }

    public void readFields(ResultSet resultSet) throws SQLException {
        this.id = resultSet.getInt(1);
        this.name = resultSet.getString(2);
        this.gender = resultSet.getString(3);
        this.number = resultSet.getString(4);
    }

    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(this.id);
        Text.writeString(dataOutput,this.name);
        dataOutput.writeChars(this.gender);
        dataOutput.writeChars(this.number);
    }

    public void readFields(DataInput dataInput) throws IOException {
        this.id = dataInput.readInt();
        this.name = Text.readString(dataInput);
        this.gender = dataInput.readLine();
        this.number = dataInput.readLine();
    }
}
