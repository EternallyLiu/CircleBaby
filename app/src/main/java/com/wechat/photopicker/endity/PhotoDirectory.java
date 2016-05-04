package com.wechat.photopicker.endity;

import java.util.ArrayList;
import java.util.List;

/**

 * 类描述：
 * 创建人：黄龙源
 * 创建时间：15/6/28 19:00
 * 修改人：黄龙源
 * 修改备注：
 */
public class PhotoDirectory {

  private String id;
  private String coverPath;
  private String name;
  private long   dateAdded;
  private List<Photo> photos = new ArrayList<>();

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof PhotoDirectory)) return false;

    PhotoDirectory directory = (PhotoDirectory) o;

    if (!id.equals(directory.id)) return false;
    return name.equals(directory.name);
  }

  @Override public int hashCode() {
    int result = id.hashCode();
    result = 31 * result + name.hashCode();
    return result;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getCoverPath() {
    return coverPath;
  }

  public void setCoverPath(String coverPath) {
    this.coverPath = coverPath;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public long getDateAdded() {
    return dateAdded;
  }

  public void setDateAdded(long dateAdded) {
    this.dateAdded = dateAdded;
  }

  public List<Photo> getPhotos() {
    return photos;
  }

  public void setPhotos(List<Photo> photos) {
    this.photos = photos;
  }

  public ArrayList<String> getPhotoPaths() {
    int size = photos.size();
    ArrayList<String> paths = new ArrayList<>(size);
    //当遍历对象时自己写的for循环快于foreach，其他情况相反
    for (int i = 0; i < size; i++) {
      Photo photo = photos.get(i);
      paths.add(photo.getPath());
    }

    return paths;
  }

  public void addPhoto(int id, String path) {
    photos.add(new Photo(id, path));
  }

}
