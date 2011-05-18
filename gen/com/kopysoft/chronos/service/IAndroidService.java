/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /home/kopy/workspace/Chronos/src/com/kopysoft/chronos/service/IAndroidService.aidl
 */
package com.kopysoft.chronos.service;
public interface IAndroidService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.kopysoft.chronos.service.IAndroidService
{
private static final java.lang.String DESCRIPTOR = "com.kopysoft.chronos.service.IAndroidService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.kopysoft.chronos.service.IAndroidService interface,
 * generating a proxy if needed.
 */
public static com.kopysoft.chronos.service.IAndroidService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.kopysoft.chronos.service.IAndroidService))) {
return ((com.kopysoft.chronos.service.IAndroidService)iin);
}
return new com.kopysoft.chronos.service.IAndroidService.Stub.Proxy(obj);
}
public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_setClockAction:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
long _arg1;
_arg1 = data.readLong();
this.setClockAction(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_setNotification:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.setNotification(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_runUpdate:
{
data.enforceInterface(DESCRIPTOR);
this.runUpdate();
reply.writeNoException();
return true;
}
case TRANSACTION_setTextNotification:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
this.setTextNotification(_arg0, _arg1);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.kopysoft.chronos.service.IAndroidService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
public void setClockAction(boolean i_type, long i_time) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((i_type)?(1):(0)));
_data.writeLong(i_time);
mRemote.transact(Stub.TRANSACTION_setClockAction, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setNotification(boolean notification) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((notification)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setNotification, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void runUpdate() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_runUpdate, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void setTextNotification(java.lang.String title, java.lang.String message) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(title);
_data.writeString(message);
mRemote.transact(Stub.TRANSACTION_setTextNotification, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_setClockAction = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_setNotification = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_runUpdate = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_setTextNotification = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
}
public void setClockAction(boolean i_type, long i_time) throws android.os.RemoteException;
public void setNotification(boolean notification) throws android.os.RemoteException;
public void runUpdate() throws android.os.RemoteException;
public void setTextNotification(java.lang.String title, java.lang.String message) throws android.os.RemoteException;
}
