package org.openremote.model.persistence;

import org.openremote.base.exception.OpenRemoteException;

public class PersistenceException extends OpenRemoteException
{
  public PersistenceException(String msg)
  {
    super(msg);
  }

  public PersistenceException(String msg, Object... params)
  {
    super(msg, params);
  }

  public PersistenceException(String msg, Throwable cause)
  {
    super(msg, cause);
  }

  public PersistenceException(String msg, Throwable cause, Object... params)
  {
    super(msg, cause, params);
  }
}
