package uci.atcnea.student.dao;

import java.util.List;
import uci.atcnea.student.dao.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "TASK".
 */
public class Task {

    private Long id;
    private Long taskIdClass;
    private String name;
    private String description;
    private Long beginDate;
    private Long endDate;
    private Boolean allowText;
    private String responseText;
    private Boolean allowResources;
    private Integer filesLimit;
    private Integer sizeLimit;
    private String note;
    private Integer taskStatus;
    private Integer taskMode;
    private long userId;
    private long lessonId;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient TaskDao myDao;

    private User user;
    private Long user__resolvedKey;

    private Lesson lesson;
    private Long lesson__resolvedKey;

    private List<TaskRecord> taskRecord;

    public Task() {
    }

    public Task(Long id) {
        this.id = id;
    }

    public Task(Long id, Long taskIdClass, String name, String description, Long beginDate, Long endDate, Boolean allowText, String responseText, Boolean allowResources, Integer filesLimit, Integer sizeLimit, String note, Integer taskStatus, Integer taskMode, long userId, long lessonId) {
        this.id = id;
        this.taskIdClass = taskIdClass;
        this.name = name;
        this.description = description;
        this.beginDate = beginDate;
        this.endDate = endDate;
        this.allowText = allowText;
        this.responseText = responseText;
        this.allowResources = allowResources;
        this.filesLimit = filesLimit;
        this.sizeLimit = sizeLimit;
        this.note = note;
        this.taskStatus = taskStatus;
        this.taskMode = taskMode;
        this.userId = userId;
        this.lessonId = lessonId;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getTaskDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTaskIdClass() {
        return taskIdClass;
    }

    public void setTaskIdClass(Long taskIdClass) {
        this.taskIdClass = taskIdClass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Long beginDate) {
        this.beginDate = beginDate;
    }

    public Long getEndDate() {
        return endDate;
    }

    public void setEndDate(Long endDate) {
        this.endDate = endDate;
    }

    public Boolean getAllowText() {
        return allowText;
    }

    public void setAllowText(Boolean allowText) {
        this.allowText = allowText;
    }

    public String getResponseText() {
        return responseText;
    }

    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }

    public Boolean getAllowResources() {
        return allowResources;
    }

    public void setAllowResources(Boolean allowResources) {
        this.allowResources = allowResources;
    }

    public Integer getFilesLimit() {
        return filesLimit;
    }

    public void setFilesLimit(Integer filesLimit) {
        this.filesLimit = filesLimit;
    }

    public Integer getSizeLimit() {
        return sizeLimit;
    }

    public void setSizeLimit(Integer sizeLimit) {
        this.sizeLimit = sizeLimit;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(Integer taskStatus) {
        this.taskStatus = taskStatus;
    }

    public Integer getTaskMode() {
        return taskMode;
    }

    public void setTaskMode(Integer taskMode) {
        this.taskMode = taskMode;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getLessonId() {
        return lessonId;
    }

    public void setLessonId(long lessonId) {
        this.lessonId = lessonId;
    }

    /** To-one relationship, resolved on first access. */
    public User getUser() {
        long __key = this.userId;
        if (user__resolvedKey == null || !user__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            UserDao targetDao = daoSession.getUserDao();
            User userNew = targetDao.load(__key);
            synchronized (this) {
                user = userNew;
            	user__resolvedKey = __key;
            }
        }
        return user;
    }

    public void setUser(User user) {
        if (user == null) {
            throw new DaoException("To-one property 'userId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.user = user;
            userId = user.getId();
            user__resolvedKey = userId;
        }
    }

    /** To-one relationship, resolved on first access. */
    public Lesson getLesson() {
        long __key = this.lessonId;
        if (lesson__resolvedKey == null || !lesson__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            LessonDao targetDao = daoSession.getLessonDao();
            Lesson lessonNew = targetDao.load(__key);
            synchronized (this) {
                lesson = lessonNew;
            	lesson__resolvedKey = __key;
            }
        }
        return lesson;
    }

    public void setLesson(Lesson lesson) {
        if (lesson == null) {
            throw new DaoException("To-one property 'lessonId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.lesson = lesson;
            lessonId = lesson.getId();
            lesson__resolvedKey = lessonId;
        }
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<TaskRecord> getTaskRecord() {
        if (taskRecord == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            TaskRecordDao targetDao = daoSession.getTaskRecordDao();
            List<TaskRecord> taskRecordNew = targetDao._queryTask_TaskRecord(id);
            synchronized (this) {
                if(taskRecord == null) {
                    taskRecord = taskRecordNew;
                }
            }
        }
        return taskRecord;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetTaskRecord() {
        taskRecord = null;
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

}