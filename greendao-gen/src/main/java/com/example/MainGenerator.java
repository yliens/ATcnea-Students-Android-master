package com.example;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;

public class MainGenerator {
    private static final String PROJECT_DIR = "/media/truecrypt1/codes/ATcnea-Students-Android-lastupdate";
    private static final String OUT_DIR = PROJECT_DIR + "/app/src/main/java";

    public static void main(String args[]) throws Exception {
        Schema schema = new Schema(1, "uci.atcnea.student.dao");

        addTables(schema);

        new DaoGenerator().generateAll(schema, OUT_DIR);
    }

    /**
     * Create tables and the relationships between them
     */
    private static void addTables(Schema schema) {
        /* entities */
        Entity user = addUser(schema);
        Entity lesson = addLesson(schema);
        Entity nomenclator= addNomenclator(schema);
        Entity resource=addResource(schema);
        Entity filerecord=addFileRecord(schema);
        Entity userLesson=addUserLesson(schema);
        Entity group=addGroup(schema);
        Entity message=addMessage(schema);
        Entity task = addTask(schema);
        Entity taskRecord = addTaskRecord(schema);
        Entity config = addConfig(schema);

        /*Propierties*/
        Property nomenclatorIdForUser=user.addLongProperty("nomenclatorId").notNull().getProperty();
        Property userIdForResource =resource.addLongProperty("userId").notNull().getProperty();
        Property userIdForFileRecord =filerecord.addLongProperty("userId").notNull().getProperty();
        Property resourceIdForFileRecord =filerecord.addLongProperty("resourceId").notNull().getProperty();
        Property userIdForUserLesson =userLesson.addLongProperty("userId").notNull().getProperty();
        Property lessonIdForUserLesson =userLesson.addLongProperty("lessonId").notNull().getProperty();
        Property lessonIdForResource = resource.addLongProperty("lessonId").notNull().getProperty();
        Property userIdForTask = task.addLongProperty("userId").notNull().getProperty();
        Property lessonIdForTask = task.addLongProperty("lessonId").notNull().getProperty();
        Property taskIdForTaskRecord = taskRecord.addLongProperty("taskId").notNull().getProperty();
        Property resourceIdForTaskRecord = taskRecord.addLongProperty("resourceId").notNull().getProperty();

        //Property userIdForMessage =message.addLongProperty("userId").getProperty();

        Property groupIdForMessage = message.addLongProperty("groupId").notNull().getProperty();

        //Esto no debe ir
        //Property messageIdForGroup = message.addLongProperty("messageId").notNull().getProperty();

        Property lessonIdForGroup = group.addLongProperty("idLesson").getProperty();

        /*Relationships*/

        //  (user) <-*------1-> (nomenclator)
        user.addToOne(nomenclator, nomenclatorIdForUser, "nomenclator");
        nomenclator.addToMany(user, nomenclatorIdForUser, "user");

        //  (resource) <-*------0:1-> (user)
        resource.addToOne(user, userIdForResource, "user");
        user.addToMany(resource, userIdForResource, "resource");

        //  (resource) <-*------0:1-> (lesson)
        resource.addToOne(lesson, lessonIdForResource, "lesson");
        lesson.addToMany(resource, lessonIdForResource, "resource");

        //  (task) <-*------1-> (user)
        task.addToOne(user,userIdForTask,"user");
        user.addToMany(task,userIdForTask,"task");

        //  (taskRecord) <-1------0:1-> (resource)
        //resource.addToOne(taskRecord, resourceIdForTaskRecord, "taskRecord");
        taskRecord.addToOne(resource, resourceIdForTaskRecord, "resource");

        //  (lesson) <-1------*-> (task)
        task.addToOne(lesson, lessonIdForTask, "lesson");
        lesson.addToMany(task, lessonIdForTask, "task");

        //  (taskRecord) <-*------1-> (task)
        taskRecord.addToOne(task, taskIdForTaskRecord, "task");
        task.addToMany(taskRecord, taskIdForTaskRecord, "taskRecord");

        //  (filerecord) <-*------1-> (user)
        filerecord.addToOne(user, userIdForFileRecord, "user");
        user.addToMany(filerecord, userIdForFileRecord, "filerecord");

        //  (filerecord) <-*------0:1-> (resource)
        filerecord.addToOne(resource, resourceIdForFileRecord, "resource");
        resource.addToMany(filerecord, resourceIdForFileRecord, "filerecord");

        //  (userLesson) <-*------0:1-> (user)
        userLesson.addToOne(user, userIdForUserLesson, "user");
        user.addToMany(userLesson, userIdForUserLesson, "userLesson");

        //  (userLesson) <-*------0:1-> (lesson)
        userLesson.addToOne(lesson, lessonIdForUserLesson, "lesson");
        lesson.addToMany(userLesson, lessonIdForUserLesson, "userLesson");

        //  (message) <-*------0:1-> (group)
        message.addToOne(group, groupIdForMessage, "group");
        group.addToMany(message, groupIdForMessage, "message");
        //group.addToMany(message, messageIdForGroup, "messages");

        //  (group) <-*------0:1-> (lesson)
        group.addToOne(lesson, lessonIdForGroup, "lesson");
        lesson.addToMany(group, lessonIdForGroup, "group");

//
//        /* properties */
//        Property userIdForUserDetails = userDetails.addLongProperty("userId").notNull().getProperty();
//        Property userDetailsIdForUser = user.addLongProperty("detailsId").notNull().getProperty();
//        Property userDetailsIdForPhoneNumber = phoneNumber.addLongProperty("detailsId").notNull().getProperty();
//
//        /* relationships between entities */
//        userDetails.addToOne(user, userIdForUserDetails, "user");    // one-to-one (user.getDetails)
//        user.addToOne(userDetails, userDetailsIdForUser, "details"); // one-to-one (user.getUser)
//
//        ToMany userDetailsToPhoneNumbers = userDetails.addToMany(phoneNumber, userDetailsIdForPhoneNumber);
//        userDetailsToPhoneNumbers.setName("phoneNumbers"); // one-to-many (userDetails.getListOfPhoneNumbers)

    }

    /**
     * Create user's Properties
     *
     * @return DBUser entity
     */
    private static Entity addUser(Schema schema) {
        Entity user = schema.addEntity("User");
        user.addIdProperty().primaryKey().autoincrement();

        user.addBooleanProperty("enable");
        user.addStringProperty("imagePath");

        user.addStringProperty("username").notNull().unique();
        user.addStringProperty("password");
        user.addStringProperty("profileJson");
        user.addStringProperty("createdAt");
        user.addStringProperty("updatedAt");
        user.addBooleanProperty("savePassword");

        return user;
    }
    private static Entity addNomenclator(Schema schema) {
        Entity nomenclator = schema.addEntity("Nomenclator");
        nomenclator.addIdProperty().primaryKey().autoincrement();
        nomenclator.addStringProperty("createdAt");
        nomenclator.addStringProperty("description");
//        nomenclator.addStringProperty("password");
        nomenclator.addStringProperty("name");
        nomenclator.addStringProperty("updatedAt");
        return nomenclator;
    }

    private static Entity addResource(Schema schema) {
        Entity resource = schema.addEntity("Resource");
        resource.addIdProperty().primaryKey().autoincrement();
        resource.addStringProperty("name");
        resource.addLongProperty("size");
        //resource.addStringProperty("createdAt");
        resource.addStringProperty("uri");
        //resource.addStringProperty("updatedAt");
        return resource;
    }
    private static Entity addFileRecord(Schema schema) {
        Entity resource = schema.addEntity("FileRecord");
        resource.addIdProperty().primaryKey().autoincrement();
//        resource.addStringProperty("name");
//        resource.addLongProperty("size");
        resource.addStringProperty("createdAt");
        resource.addLongProperty("transferId");

        return resource;
    }

    /**
     * Create user details Properties
     *
     * @return DBUserDetails entity
     */
    private static Entity addLesson(Schema schema) {
        Entity lesson = schema.addEntity("Lesson");
        lesson.addIdProperty().primaryKey().autoincrement();
        //  lesson.addLongProperty("id").notNull().unique().primaryKey().autoincrement();
        lesson.addLongProperty("idLesson").notNull();
        lesson.addStringProperty("ipDireccion").notNull();
        lesson.addStringProperty("macDireccion").notNull();
        lesson.addStringProperty("lessonName").notNull();
        lesson.addStringProperty("capacityState").notNull();
        lesson.addStringProperty("teacherName").notNull();
        lesson.addStringProperty("authenticationType").notNull();
        lesson.addByteArrayProperty("imageTeacher");

        return lesson;
    }

    private static Entity addMessage(Schema schema) {
        Entity message = schema.addEntity("Message");
        message.addIdProperty().primaryKey().autoincrement();
        //  lesson.addLongProperty("id").notNull().unique().primaryKey().autoincrement();
        message.addStringProperty("message").notNull();
        message.addStringProperty("userName").notNull();
        message.addStringProperty("createdAt").notNull();
        return message;
    }

    private static Entity addGroup(Schema schema) {
        Entity group = schema.addEntity("Group");
        group.addIdProperty().primaryKey().autoincrement();
        //lesson.addLongProperty("id").notNull().unique().primaryKey().autoincrement();
        group.addStringProperty("name").notNull();
        group.addDateProperty("createdAt").notNull();
        group.addDateProperty("updatedAt").notNull();
        return group;
    }
    private static Entity addUserLesson(Schema schema) {
        Entity lesson = schema.addEntity("UserLesson");
        lesson.addIdProperty().primaryKey().autoincrement();

        return lesson;
    }
    private static Entity addTask(Schema schema){
        Entity task = schema.addEntity("Task");
        task.addIdProperty().primaryKey().autoincrement();

        task.addLongProperty("taskIdClass");
        task.addStringProperty("name");
        task.addStringProperty("description");
        task.addLongProperty("beginDate");
        task.addLongProperty("endDate");
        task.addBooleanProperty("allowText");
        task.addStringProperty("responseText");
        task.addBooleanProperty("allowResources");
        task.addIntProperty("filesLimit");
        task.addIntProperty("sizeLimit");
        task.addStringProperty("note");

        task.addIntProperty("taskStatus");
        task.addIntProperty("taskMode");

        return task;
    }

    private static Entity addTaskRecord(Schema schema){
        Entity taskRecord = schema.addEntity("TaskRecord");
        taskRecord.addIdProperty().primaryKey().autoincrement();

        taskRecord.addBooleanProperty("resourceStatus");

        return taskRecord;
    }

    private static Entity addConfig(Schema schema){
        Entity config = schema.addEntity("Config");
        config.addIdProperty().primaryKey().autoincrement();

        config.addStringProperty("macDevices");

        return config;
    }
}
