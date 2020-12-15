//package com.allstart.common.jpa.dao;
//
//import org.apache.commons.beanutils.BeanUtils;
//import org.hibernate.*;
//import org.hibernate.annotations.FlushModeType;
//import org.hibernate.transform.Transformers;
//import org.hibernate.type.StandardBasicTypes;
//import org.springframework.orm.hibernate5.HibernateCallback;
//import org.springframework.orm.hibernate5.SessionFactoryUtils;
//import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
//import org.springframework.transaction.annotation.Transactional;
//
//import javax.persistence.Column;
//import java.io.Serializable;
//import java.lang.InstantiationException;
//import java.lang.reflect.Field;
//import java.lang.reflect.InvocationTargetException;
//import java.math.BigDecimal;
//import java.math.BigInteger;
//import java.sql.Timestamp;
//import java.util.*;
//
//import static javax.persistence.FlushModeType.COMMIT;
//import static org.hibernate.FlushMode.AUTO;
//
///**
// * $Id: GenericDAO.java,v 1.3 2010/08/01 14:01:59 wangshuang Exp $
// */
//
//@SuppressWarnings("unchecked")
//public class GenericDAO extends HibernateDaoSupport {
//
//    public GenericDAO() {
//    }
//
//    /**
//     * @param c  class
//     * @param id id
//     * @return T
//     */
//
//    public <T> T get(final Class c, final Serializable id) {
//        assert getHibernateTemplate() != null;
//        return (T) getHibernateTemplate().execute((HibernateCallback) session -> {
//            Object t = session.get(c, id);
//            sessionClear(session);
//            return t;
//        });
//    }
//
//    public <T> T load(final Class c, final Serializable id) {
//        assert getHibernateTemplate() != null;
//        return (T) getHibernateTemplate().execute((HibernateCallback) session -> {
//            Object t = session.load(c, id);
//            sessionClear(session);
//            return t;
//        });
//    }
//
//    public <T> T get(String hql, Object... values) {
//        List<T> list = this.list(hql, values);
//        if (list != null && list.size() > 0) {
//            return list.get(0);
//        } else {
//            return null;
//        }
//    }
//
//    public <T> T save(T obj) {
//        assert getHibernateTemplate() != null;
//        getHibernateTemplate().execute((HibernateCallback) session -> {
//            session.save(obj);
//            return obj;
//        });
//        return obj;
//    }
//
//    public <T> T save(String entityName, T obj) {
//
//        assert getHibernateTemplate() != null;
//        getHibernateTemplate().execute((HibernateCallback) session -> {
//            getHibernateTemplate().save(entityName, obj);
//            return obj;
//        });
//
//        return obj;
//    }
//
//    public <T> T update(final T obj) {
//
//
//
//
//
//        assert getHibernateTemplate() != null;
//        getHibernateTemplate().execute((HibernateCallback) session -> {
//            session.update(obj);
//            return obj;
//        });
//        return obj;
//    }
//
//    /**
//     * saveOrUpdate
//     *
//     * @return
//     */
//
//    public <T> T saveOrUpdate(final T obj) {
//        assert getHibernateTemplate() != null;
//        getHibernateTemplate().execute((HibernateCallback) session -> {
//            session.saveOrUpdate(obj);
//            return obj;
//        });
//        return obj;
//    }
//
//    public <T> void delete(T obj) {
//        assert getHibernateTemplate() != null;
//        getHibernateTemplate().delete(obj);
//    }
//
//    /**
//     * @param list
//     */
//    public <T> void batchSaveOrUpdate(List<T> list) {
//        assert getHibernateTemplate() != null;
//        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
//        Transaction tx = session.beginTransaction();
//        for (int i = 0; i < list.size(); i++) {
//            session.save(list.get(i));
//            if (i % 100 == 0) {
//                session.flush();
//                session.clear();
//            }
//        }
//        tx.commit();
//        sessionClear(session);
//    }
//
//    /**
//     * hql（）
//     *
//     * @param hql    hql
//     * @param values
//     * @return
//     */
//
//    public List find(final String hql, final Object... values) {
//        HibernateCallback hibernateCallback = session -> {
//            Query query = session.createQuery(hql);
//            if (values != null && values.length > 0) {
//                for (int i = 0; i < values.length; i++) {
//                    query.setParameter(i+1, values[i]);
//                }
//            }
//            List list = query.list();
//            sessionClear(session);
//            return list;
//        };
//        assert getHibernateTemplate() != null;
//        return (List) getHibernateTemplate().execute(hibernateCallback);
//        // return getHibernateTemplate().find(hql, values);
//    }
//
//    /**
//     * HQL（）
//     *
//     * @param hql    hql
//     * @param values
//     * @return
//     */
//
//    public <T> List<T> list(final String hql, final Object... values) {
//        HibernateCallback hibernateCallback = session -> {
//            Query query = session.createQuery(hql);
//            if (values != null && values.length > 0) {
//                for (int i = 0; i < values.length; i++) {
//                    query.setParameter(i+1, values[i]);
//                }
//            }
//            List<T> list = query.list();
//            sessionClear(session);
//            return list;
//        };
//        assert getHibernateTemplate() != null;
//        return (List<T>) getHibernateTemplate().execute(hibernateCallback);
//    }
//
//    public Long getCount(String hql, Object... values) {
//        long res = 0;
//        List<Long> list = this.find(hql, values);
//        if (list != null && list.size() > 0) {
//            res = list.get(0).longValue();
//        }
//        return res;
//    }
//
//    public Long getSqlCount(String sql, Object... values) {
//        long res = 0;
//        List<BigInteger> list = createSQLQuery(sql, values);
//        if (list != null && list.size() > 0) {
//            if (list.get(0) == null) {
//                return res;
//            }
//            res = list.get(0).longValue();
//        }
//        return res;
//    }
//
//    public long getSqlSum(String sql, Object... values) {
//        BigDecimal res = new BigDecimal(0);
//        List<BigDecimal> list = createSQLQuery(sql, values);
//        if (list != null && list.size() > 0) {
//            if (list.get(0) == null) {
//                return res.longValue();
//            }
//            res = list.get(0);
//        }
//        return res.longValue();
//    }
//
//    public int getSqlInt(String sql, Object... values) {
//        int res = 0;
//        List<BigInteger> list = createSQLQuery(sql, values);
//        if (list != null && list.size() > 0) {
//            if (list.get(0) == null) {
//                return res;
//            }
//            BigInteger bigint=(BigInteger)list.get(0);
//            res = bigint.intValue();
////            res = list.get(0).intValue();
//        }
//        return res;
//    }
//
//    public Timestamp getSqlDate(String sql, Object... values) {
//        Timestamp res = null;
//        List<Timestamp> list = createSQLQuery(sql, values);
//        if (list != null && list.size() > 0) {
//            res = list.get(0);
//        }
//        return res;
//    }
//
//    public Long getSqlCountBigDecimal(String sql, Object... values) {
//        long res = 0;
//        List<BigDecimal> list = createSQLQuery(sql, values);
//        if (list != null && list.size() > 0) {
//            res = list.get(0).longValue();
//        }
//        return res;
//    }
//
//    public int executeHQL(final String hql, final Object... values) {
//        return (Integer) getHibernateTemplate().execute((HibernateCallback) s -> {
//            Transaction transaction =s.beginTransaction();
//            Query query = s.createQuery(hql);
//            if (values != null && values.length > 0) {
//                for (int p = 0; p < values.length; p++) {
//                    if (values[p] != null) {
//                        query.setParameter(p+1, values[p]);
//                    }
//                }
//            }
//            int res = query.executeUpdate();
//            if(res>0){
//                transaction.commit();
//            }
//            return res;
//        });
//    }
//
//    /**
//     * SQL
//     *
//     * @param sql    sql
//     * @param clazz
//     * @param values
//     * @return
//     */
//
//    public <T> List<T> sqlQuery(final String sql, final Class clazz, final Object... values) {
//        return (List) getHibernateTemplate().execute((HibernateCallback) session -> {
//            SQLQuery query = session.createSQLQuery(sql);
//            query.addEntity(clazz);
//            if (values != null && values.length > 0) {
//                for (int i = 0; i < values.length; i++) {
//                    query.setParameter(i + 1, values[i]);
//                }
//            }
//            return query.list();
//        });
//    }
//
//    /**
//     * @param sql
//     * @param clazz
//     * @param values
//     * @return List<T>
//     * @throws
//     * @Title: sqlGetId
//     * @Description: sqlidlist
//     */
//
//    public <T> List<T> sqlGetId(final String sql, final Class<T> clazz, final Object... values) {
//        return (List) getHibernateTemplate().execute((HibernateCallback) session -> {
//            SQLQuery query = session.createSQLQuery(sql);
//            // query.addEntity(clazz);
//            if (values != null && values.length > 0) {
//                for (int i = 0; i < values.length; i++) {
//                    query.setParameter(i+1, values[i]);
//                }
//            }
//            return query.list();
//        });
//    }
//
//    public List<Object[]> sqlProc(final String sql, final Class clazz, final Object... values) {
//        return (List<Object[]>) getHibernateTemplate().execute((HibernateCallback) session -> {
//            List<Object[]> list = null;
//            SQLQuery query = session.createSQLQuery(sql);
//            if (values != null && values.length > 0) {
//                for (int i = 0; i < values.length; i++) {
//                    query.setParameter(i+1, values[i]);
//                }
//            }
//            return query.list();
//        });
//    }
//
//    /**
//     * sql
//     *
//     * @param sql    sql
//     * @param fetch
//     * @param max
//     * @param clazz
//     * @param params
//     * @return
//     */
//
//    public <T> List<T> sqlQuery(final String sql, final int fetch, final int max, final Class clazz, final Object... params) {
//        return (List) getHibernateTemplate().execute((HibernateCallback) session -> {
//            List list;
//            SQLQuery query = session.createSQLQuery(sql);
//            query.addEntity(clazz);
//            if (params != null && params.length > 0) {
//                for (int i = 0; i < params.length; i++) {
//                    query.setParameter(i+1, params[i]);
//                }
//            }
//            query.setFetchSize(fetch);
//            query.setMaxResults(max);
//            list = query.list();
//            return list;
//        });
//    }
//
//    @SuppressWarnings("unchecked")
//    public <T> List<T> sqlQueryPOJO(final String sql, final Class sclass, final Object... objs) {
//        return (List<T>) this.getHibernateTemplate().execute((HibernateCallback) session -> {
//            List list;
//            List l;
//            try {
//                if (sclass == null) {
//                    throw new Exception("Class sclass is null!!!");
//                }
//                SQLQuery query = session.createSQLQuery(sql);
//                query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
//                for (int i = 0; i < objs.length; i++) {
//                    query.setParameter(i+1, objs[i]);
//                }
//                list = query.list();
//                l = getDataFromMap(list, sclass);
//            } catch (Exception e) {
//                e.printStackTrace();
//                return null;
//            }
//
//            return l;
//        });
//    }
//
//    public <T> List<T> sqlQueryPOJOLimit(final String sql, final Class sclass, final int fetch, final int max,
//                                         final Object... objs) {
//        return (List<T>) this.getHibernateTemplate().execute((HibernateCallback) session -> {
//            List list;
//            List l;
//            try {
//                if (sclass == null) {
//                    throw new Exception("Class sclass is null!!!");
//                }
//                SQLQuery query = session.createSQLQuery(sql);
//                query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
//                query.setFirstResult(fetch);
//                query.setMaxResults(max);
//                for (int i = 0; i < objs.length; i++) {
//                    query.setParameter(i+1, objs[i]);
//                }
//                list = query.list();
//                l = getDataFromMap(list, sclass);
//            } catch (Exception e) {
//                e.printStackTrace();
//                return null;
//            }
//
//            return l;
//        });
//    }
//
//    private List getDataFromMap(List<Map<String, Object>> list, Class sclass) throws InstantiationException,
//            IllegalAccessException, ClassNotFoundException, InvocationTargetException {
//        List l = new ArrayList();
//        if (list != null && list.size() > 0) {
//
//            Field[] fs = sclass.getDeclaredFields();
//            for (Map<String, Object> map : list) {
//                Object obj = Class.forName(sclass.getName()).newInstance();
//                for (Field f : fs) {
//                    String n = f.getName();
//                    if ("serialVersionUID".equals(n)) {
//                        continue;
//                    }
//                    Object v = map.get(n);
//                    if (v != null) {
//                        if ("java.sql.Timestamp".equals(v.getClass().getName())
//                                && "java.util.Date".equals(f.getType().getName())) {
//                            Timestamp t = (Timestamp) v;
//                            Calendar c = Calendar.getInstance();
//                            c.setTimeInMillis(t.getTime());
//                            v = c.getTime();
//                        }
//                        BeanUtils.setProperty(obj, n, v);
//                    } else {
//                        Column column = f.getAnnotation(Column.class);
//                        String str = n;
//                        if (column == null) {
//                            str = str.replaceFirst(str.substring(0, 1), str.substring(0, 1).toUpperCase());
//                            try {
//                                column = sclass.getMethod("get" + str).getAnnotation(Column.class);
//                            } catch (NoSuchMethodException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                        if (column == null) {
//                            continue;
//                        }
//                        str = column.name();
//                        v = map.get(str);
//                        if (v != null) {
//                            BeanUtils.setProperty(obj, n, v);
//                        }
//                    }
//                }
//                l.add(obj);
//            }
//        }
//        return l;
//    }
//
//    /**
//     * @param sql
//     * @param fetch
//     * @param max
//     * @param clazz
//     * @param params
//     * @return List<T>
//     * @throws
//     * @Title: sqlQuery
//     * @Description:sqlid
//     */
//
//    public <T> List<T> sqlgetId(final String sql, final int fetch, final int max, final Class<T> clazz, final Object... params) {
//        assert getHibernateTemplate() != null;
//        return (List<T>) getHibernateTemplate().execute((HibernateCallback) session -> {
//            SQLQuery query = session.createSQLQuery(sql);
//            query.addEntity(clazz);
//            if (params != null && params.length > 0) {
//                for (int i = 0; i < params.length; i++) {
//                    query.setParameter(i+1, params[i]);
//                }
//            }
//            query.setFirstResult(fetch);
//            query.setMaxResults(max);
//            return query.list();
//        });
//    }
//
//    /**
//     * sql，
//     *
//     * @param sql  sql
//     * @param objs
//     * @return
//     */
//    public int sqlUpdate(final String sql, final Object... objs) {
//        assert this.getHibernateTemplate() != null;
//        return (Integer) this.getHibernateTemplate().execute((HibernateCallback) session -> {
//            SQLQuery query = session.createSQLQuery(sql);
//            Transaction transaction =session.beginTransaction();
//            if (objs != null && objs.length > 0) {
//                for (int i = 0; i < objs.length; i++) {
//                    query.setParameter(i+1, objs[i]);
//                }
//            }
//            Integer result = query.executeUpdate();
//            if(Objects.nonNull(result)){
//                transaction.commit();
//            }
//            return result;
//
//        });
//    }
//
//    /**
//     * hql
//     *
//     * @param hql    hql
//     * @param first
//     * @param count
//     * @param values
//     * @return
//     */
//
//    public <T> List<T> findLimitList(final String hql, final int first, final int count, final Object... values) {
//        return (List<T>) getHibernateTemplate().execute((HibernateCallback) s -> {
//            Query query = s.createQuery(hql);
//            if (values != null && values.length > 0) {
//                for (int p = 0; p < values.length; p++) {
//                    if (values[p] != null) {
//                        query.setParameter(p+1, values[p]);
//                    }
//                }
//            }
//            query.setFirstResult(first);
//            query.setMaxResults(count);
//            List<T> list = query.list();
//            sessionClear(s);
//            return list;
//        });
//    }
//
//    /**
//     * sqlInsert sql，
//     *
//     * @param sql
//     * @param objs
//     * @return
//     * @author zhuzhigang
//     * @since 2010-04-07
//     */
//    public Long sqlInsert(final String sql, final Object[] objs) {
//        return (Long) this.getHibernateTemplate().execute((HibernateCallback) session -> {
//            // Transaction tx = session.beginTransaction();
//            SQLQuery query = session.createSQLQuery(sql);
//            for (int i = 0; i < objs.length; i++) {
//                query.setParameter(i+1, objs[i]);
//            }
//            query.executeUpdate();
//            String sqlid = "select LAST_INSERT_ID() as id";
//            query = session.createSQLQuery(sqlid);
//            // tx.commit();
//            query.addScalar("id", StandardBasicTypes.LONG);
//            List list = query.list();
//            Long id = 0L;
//            if (list.get(0) != null) {
//                id = (Long) list.get(0);
//            }
//            return id;
//        });
//    }
//
//    /*
//     * (non-Javadoc)
//     *
//     * @see
//     * com.portal.common.persistence.dao.IEntityDAO#findByHQLandPage(java.lang
//     * .String, ,Page page, java.lang.Object[])
//     */
//    //
//    // public List<T> findByHQLandPage(final String hql, final Page page,
//    // final Object... values) {
//    //
//    // return (List) getHibernateTemplate().execute(new HibernateCallback() {
//    // public Object doInHibernate(Session s) throws HibernateException,
//    // SQLException {
//    // List list;
//    // try {
//    // Query query = s.createQuery(hql);
//    // for (int p = 0; p < values.length; p++) {
//    // if (values[p] != null) {
//    // query.setParameter(p, values[p]);
//    // }
//    // }
//    // query.setFirstResult(page.getFirstItemPos());
//    // query.setMaxResults(page.getPageSize());
//    // list = query.list();
//    // sessionClear(s);
//    // } finally {
//    // releaseSession(s);
//    // }
//    // return list;
//    // }
//    // });
//    // }
//
//    /*
//     * (non-Javadoc)
//     */
//    public List createQuery(final String hql, final Object... values) {
//
//        return (List) getHibernateTemplate().execute((HibernateCallback) s -> {
//
//            List list;
//            Query query = s.createQuery(hql);
//            for (int i = 0; i < values.length; i++) {
//                query.setParameter(i+1, values[i]);
//            }
//            list = query.list();
//            sessionClear(s);
//
//            return list;
//        });
//    }
//
//    public List createSQLQuery(final String sql, final Object... values) {
//        return (List) getHibernateTemplate().execute((HibernateCallback) s -> {
//            List list;
//            SQLQuery query = s.createSQLQuery(sql);
//            if (values != null && values.length > 0) {
//                for (int i = 0; i < values.length; i++) {
//                    query.setParameter(i+1, values[i]);
//                }
//            }
//            list = query.list();
//            return list;
//        });
//    }
//
//    public Object sqlQueryUnique(final String sql, final Object... values) {
//        assert getHibernateTemplate() != null;
//        return getHibernateTemplate().execute((HibernateCallback) s -> {
//            Object obj;
//            SQLQuery query = s.createSQLQuery(sql);
//            for (int i = 0; i < values.length; i++) {
//                query.setParameter(i+1, values[i]);
//            }
//            return query.uniqueResult();
//        });
//    }
//
//    private void sessionClear(Session session) {
//        session.clear();
//    }
//
//    public int hqlUpdate(final String hql, final Object... values) {
//
//        return (Integer) getHibernateTemplate().execute((HibernateCallback) s -> {
//            Query query = s.createQuery(hql);
//            for (int i = 0; i < values.length; i++) {
//                query.setParameter(i+1, values[i]);
//            }
//            return query.executeUpdate();
//        });
//    }
//}
