package main.command;

import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import uci.atcnea.core.interfaces.CommandInterface;
import uci.atcnea.core.networking.OperationResult;
import uci.atcnea.student.MainApp;

/**
 * @class   LessonInformation
 * @version 1.0
 * @date 7/07/16
 * @author Adrian Arencibia Herrera
 * Copyright (c) 2016-2017 FORTES, UCI.
 *
 * @description
 * @rf
 */
public class LessonInformation implements CommandInterface,Serializable {
    private static final long serialVersionUID = 211L;

    public LessonInformation() {
    }

    /**
     *
     */
    public class ImageTeacher  {
        private static final long serialVersionUID = 2131L;
        private byte[] imageTeacher;

        public ImageTeacher() {
        }

        public byte[] getImageTeacher() {
            return imageTeacher;
        }
        public void setImageTeacher(byte[] imageTeacher) {
            this.imageTeacher = imageTeacher;
        }
    }

    /**
     *
     * @param applicationContext
     * @return
     */
    @Override
    public OperationResult execute(Context applicationContext) {
        return null;
    }
}
