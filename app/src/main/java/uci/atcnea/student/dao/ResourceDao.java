package uci.atcnea.student.dao;

import java.util.List;
import java.util.ArrayList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.SqlUtils;
import de.greenrobot.dao.internal.DaoConfig;
import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

import uci.atcnea.student.dao.Resource;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "RESOURCE".
*/
public class ResourceDao extends AbstractDao<Resource, Long> {

    public static final String TABLENAME = "RESOURCE";

    /**
     * Properties of entity Resource.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Name = new Property(1, String.class, "name", false, "NAME");
        public final static Property Size = new Property(2, Long.class, "size", false, "SIZE");
        public final static Property Uri = new Property(3, String.class, "uri", false, "URI");
        public final static Property UserId = new Property(4, long.class, "userId", false, "USER_ID");
        public final static Property LessonId = new Property(5, long.class, "lessonId", false, "LESSON_ID");
    };

    private DaoSession daoSession;

    private Query<Resource> user_ResourceQuery;
    private Query<Resource> lesson_ResourceQuery;

    public ResourceDao(DaoConfig config) {
        super(config);
    }
    
    public ResourceDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"RESOURCE\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"NAME\" TEXT," + // 1: name
                "\"SIZE\" INTEGER," + // 2: size
                "\"URI\" TEXT," + // 3: uri
                "\"USER_ID\" INTEGER NOT NULL ," + // 4: userId
                "\"LESSON_ID\" INTEGER NOT NULL );"); // 5: lessonId
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"RESOURCE\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Resource entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(2, name);
        }
 
        Long size = entity.getSize();
        if (size != null) {
            stmt.bindLong(3, size);
        }
 
        String uri = entity.getUri();
        if (uri != null) {
            stmt.bindString(4, uri);
        }
        stmt.bindLong(5, entity.getUserId());
        stmt.bindLong(6, entity.getLessonId());
    }

    @Override
    protected void attachEntity(Resource entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Resource readEntity(Cursor cursor, int offset) {
        Resource entity = new Resource( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // name
            cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2), // size
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // uri
            cursor.getLong(offset + 4), // userId
            cursor.getLong(offset + 5) // lessonId
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Resource entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setSize(cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2));
        entity.setUri(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setUserId(cursor.getLong(offset + 4));
        entity.setLessonId(cursor.getLong(offset + 5));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Resource entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Resource entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
    /** Internal query to resolve the "resource" to-many relationship of User. */
    public List<Resource> _queryUser_Resource(long userId) {
        synchronized (this) {
            if (user_ResourceQuery == null) {
                QueryBuilder<Resource> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.UserId.eq(null));
                user_ResourceQuery = queryBuilder.build();
            }
        }
        Query<Resource> query = user_ResourceQuery.forCurrentThread();
        query.setParameter(0, userId);
        return query.list();
    }

    /** Internal query to resolve the "resource" to-many relationship of Lesson. */
    public List<Resource> _queryLesson_Resource(long lessonId) {
        synchronized (this) {
            if (lesson_ResourceQuery == null) {
                QueryBuilder<Resource> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.LessonId.eq(null));
                lesson_ResourceQuery = queryBuilder.build();
            }
        }
        Query<Resource> query = lesson_ResourceQuery.forCurrentThread();
        query.setParameter(0, lessonId);
        return query.list();
    }

    private String selectDeep;

    protected String getSelectDeep() {
        if (selectDeep == null) {
            StringBuilder builder = new StringBuilder("SELECT ");
            SqlUtils.appendColumns(builder, "T", getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T0", daoSession.getUserDao().getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T1", daoSession.getLessonDao().getAllColumns());
            builder.append(" FROM RESOURCE T");
            builder.append(" LEFT JOIN USER T0 ON T.\"USER_ID\"=T0.\"_id\"");
            builder.append(" LEFT JOIN LESSON T1 ON T.\"LESSON_ID\"=T1.\"_id\"");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected Resource loadCurrentDeep(Cursor cursor, boolean lock) {
        Resource entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        User user = loadCurrentOther(daoSession.getUserDao(), cursor, offset);
         if(user != null) {
            entity.setUser(user);
        }
        offset += daoSession.getUserDao().getAllColumns().length;

        Lesson lesson = loadCurrentOther(daoSession.getLessonDao(), cursor, offset);
         if(lesson != null) {
            entity.setLesson(lesson);
        }

        return entity;    
    }

    public Resource loadDeep(Long key) {
        assertSinglePk();
        if (key == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder(getSelectDeep());
        builder.append("WHERE ");
        SqlUtils.appendColumnsEqValue(builder, "T", getPkColumns());
        String sql = builder.toString();
        
        String[] keyArray = new String[] { key.toString() };
        Cursor cursor = db.rawQuery(sql, keyArray);
        
        try {
            boolean available = cursor.moveToFirst();
            if (!available) {
                return null;
            } else if (!cursor.isLast()) {
                throw new IllegalStateException("Expected unique result, but count was " + cursor.getCount());
            }
            return loadCurrentDeep(cursor, true);
        } finally {
            cursor.close();
        }
    }
    
    /** Reads all available rows from the given cursor and returns a list of new ImageTO objects. */
    public List<Resource> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<Resource> list = new ArrayList<Resource>(count);
        
        if (cursor.moveToFirst()) {
            if (identityScope != null) {
                identityScope.lock();
                identityScope.reserveRoom(count);
            }
            try {
                do {
                    list.add(loadCurrentDeep(cursor, false));
                } while (cursor.moveToNext());
            } finally {
                if (identityScope != null) {
                    identityScope.unlock();
                }
            }
        }
        return list;
    }
    
    protected List<Resource> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }
    

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<Resource> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }
 
}
