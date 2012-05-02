package cz.jirimasek.dppstops.dao;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

/**
 * Třída <code>PMF</code> vrací <code>PersistenceManagerFactory</code>.
 * 
 * @author masekj@gmail.com
 */
public final class PMF
{
    private static final PersistenceManagerFactory pmfInstance =
        JDOHelper.getPersistenceManagerFactory("transactions-optional");

    private PMF() {}

    public static PersistenceManagerFactory get() {
        return pmfInstance;
    }
}
