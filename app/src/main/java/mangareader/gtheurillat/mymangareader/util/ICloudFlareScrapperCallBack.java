package mangareader.gtheurillat.mymangareader.util;

public interface ICloudFlareScrapperCallBack<T> {
    public void onSuccess(T object);
    public void onFailure(Exception e);
}
