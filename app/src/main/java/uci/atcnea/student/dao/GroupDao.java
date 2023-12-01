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

import uci.atcnea.student.dao.Group;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "GROUP".
*/
public class GroupDao extends AbstractDao<Group, Long> {

    public static final String TABLENAME = "GROUP";

    /**
     * Properties of entity Group.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Name = new Property(1, String.class, "name", false, "NAME");
        public final static Property CreatedAt = new Property(2, java.util.Date.class, "createdAt", false, "CREATED_AT");
        public final static Property UpdatedAt = new Property(3, java.util.Date.class, "updatedAt", false, "UPDATED_AT");
        public final static Property IdLesson = new Property(4, Long.class, "idLesson", false, "ID_LESSON");
    };

    private DaoSession daoSession;

    private Query<Group> lesson_GroupQuery;

    public GroupDao(DaoConfig config) {
        super(config);
    }
    
    public GroupDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"GROUP\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"NAME\" TEXT NOT NULL ," + // 1: name
                "\"CREATED_AT\" INTEGER NOT NULL ," + // 2: createdAt
                "\"UPDATED_AT\" INTEGER NOT NULL ," + // 3: updatedAt
                "\"ID_LESSON\" INTEGER);"); // 4: idLesson
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"GROUP\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Group entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getName());
        stmt.bindLong(3, entity.getCreatedAt().getTime());
        stmt.bindLong(4, entity.getUpdatedAt().getTime());
 
        Long idLesson = entity.getIdLesson();
        if (idLesson != null) {
            stmt.bindLong(5, idLesson);
        }
    }

    @Override
    protected void attachEntity(Group entity) {
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
    public Group readEntity(Cursor cursor, int offset) {
        Group entity = new Group( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // name
            new java.util.Date(cursor.getLong(offset + 2)), // createdAt
            new java.util.Date(cursor.getLong(offset + 3)), // updatedAt
            cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4) // idLesson
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Group entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setName(cursor.getString(offset + 1));
        entity.setCreatedAt(new java.util.Date(cursor.getLong(offset + 2)));
        entity.setUpdatedAt(new java.util.Date(cursor.getLong(offset + 3)));
        entity.setIdLesson(cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Group entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Group entity) {
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
    
    /** Internal query to resolve the "group" to-many relationship of Lesson. */
    public List<Group> _queryLesson_Group(Long idLesson) {
        synchronized (this) {
            if (lesson_GroupQuery == null) {
                QueryBuilder<Group> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.IdLesson.eq(null));
                lesson_GroupQuery = queryBuilder.build();
            }
        }
        Query<Group> query = lesson_GroupQuery.forCurrentThread();
        query.setParameter(0, idLesson);
        return query.list();
    }

    private String selectDeep;

    protected String getSelectDeep() {
        if (selectDeep == null) {
            StringBuilder builder = new StringBuilder("SELECT ");
            SqlUtils.appendColumns(builder, "T", getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T0", daoSession.getLessonDao().getAllColumns());
            builder.append(" FROM GROUP T");
            builder.append(" LEFT JOIN LESSON T0 ON T.\"ID_LESSON\"=T0.\"_id\"");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected Group loadCurrentDeep(Cursor cursor, boolean lock) {
        Group entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        Lesson lesson = loadCurrentOther(daoSession.getLessonDao(), cursor, offset);
        entity.setLesson(lesson);

        return entity;    
    }

    public Group loadDeep(Long key) {
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
    public List<Group> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<Group> list = new ArrayList<Group>(count);
        
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
    
    protected List<Group> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }
    

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<Group> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }
 
}