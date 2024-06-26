package com.celatus.models;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.celatus.App;
import com.celatus.interfaces.Recordable;
import com.celatus.util.CryptoUtils;
import com.celatus.util.CustomDateUtils;

/** Object used to store all of the attributes and information related to our password */
public class PasswordEntry implements Recordable {

  // region =====Variables=====

  private String id;
  private String name;
  private String url; // website url
  private String notes;
  private String identifier;
  private String email; // not always the same as the identifier
  private String password;
  private LocalDateTime creationDate;
  private LocalDateTime lastEditDate;
  private Map<String, Map<String, String>> records;

  // endregion

  // region =====Getters and Setters=====

  public String getId() {
    return id;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getName() {
    return name;
  }

  public void setName(String value) {
    this.name = value.strip();
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public LocalDateTime getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(LocalDateTime creationDate) {
    this.creationDate = creationDate;
  }

  public LocalDateTime getLastEditDate() {
    return lastEditDate;
  }

  public void setLastEditDate(LocalDateTime lastEditDate) {
    this.lastEditDate = lastEditDate;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  public String getIdentifier() {
    return identifier;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Map<String, Map<String, String>> getRecords() {
    return records;
  }

  // endregion

  // region =====Constructors=====

  public PasswordEntry() {}

  public PasswordEntry(
      String name, String url, String notes, String identifier, String email, String password) {
    this.creationDate = this.lastEditDate = LocalDateTime.now();
    this.name = name;
    if (StringUtils.isNotBlank(url)) {
      this.url = url;
    }
    if (StringUtils.isNotBlank(notes)) {
      this.notes = notes;
    }
    this.identifier = identifier;
    if (StringUtils.isNotBlank(email)) {
      this.email = email;
    }
    this.password = password;
    calculateID();
  }

  // endregion

  // region =====Instance Methods=====

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((url == null) ? 0 : url.hashCode());
    result = prime * result + ((notes == null) ? 0 : notes.hashCode());
    result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
    result = prime * result + ((email == null) ? 0 : email.hashCode());
    result = prime * result + ((password == null) ? 0 : password.hashCode());
    result = prime * result + ((creationDate == null) ? 0 : creationDate.hashCode());
    result = prime * result + ((lastEditDate == null) ? 0 : lastEditDate.hashCode());
    result = prime * result + ((records == null) ? 0 : records.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    PasswordEntry other = (PasswordEntry) obj;
    if (id == null) {
      if (other.id != null) return false;
    } else if (!id.equals(other.id)) return false;
    if (name == null) {
      if (other.name != null) return false;
    } else if (!name.equals(other.name)) return false;
    if (url == null) {
      if (other.url != null) return false;
    } else if (!url.equals(other.url)) return false;
    if (notes == null) {
      if (other.notes != null) return false;
    } else if (!notes.equals(other.notes)) return false;
    if (identifier == null) {
      if (other.identifier != null) return false;
    } else if (!identifier.equals(other.identifier)) return false;
    if (email == null) {
      if (other.email != null) return false;
    } else if (!email.equals(other.email)) return false;
    if (password == null) {
      if (other.password != null) return false;
    } else if (!password.equals(other.password)) return false;
    if (creationDate == null) {
      if (other.creationDate != null) return false;
    } else if (!creationDate.equals(other.creationDate)) return false;
    if (lastEditDate == null) {
      if (other.lastEditDate != null) return false;
    } else if (!lastEditDate.equals(other.lastEditDate)) return false;
    if (records == null) {
      if (other.records != null) return false;
    } else if (!records.equals(other.records)) return false;
    return true;
  }

  @Override
  public String toString() {
    return "PasswordEntry [id="
        + id
        + ", name="
        + name
        + ", url="
        + url
        + ", notes="
        + notes
        + ", identifier="
        + identifier
        + ", email="
        + email
        + ", password=********"
        + ", creationDate="
        + creationDate
        + ", lastEditDate="
        + lastEditDate
        + ", records="
        + records
        + "]";
  }

  public Category findCategory() {
    PasswordsDatabase database = App.getPasswordsDatabase();
    for (Category cat : database.getCategories()) {
      if (cat.hasPasswordEntry(this)) {
        return cat;
      }
    }
    return null;
  }

  // endregion

  // region =====Interface Methods=====

  public void calculateID() {
    long creationDateTimeStamp = this.creationDate.atZone(ZoneId.systemDefault()).toEpochSecond();
    String toBeHashed = String.valueOf(creationDateTimeStamp) + this.name;
    this.id = "PE" + CryptoUtils.getSHA256Hash(toBeHashed).toUpperCase().substring(0, 8);
  }

  public void saveRecord(String changes) {
    Map<String, String> record = new HashMap<>();
    record.put("name", this.name);
    record.put("identifier", this.identifier);
    record.put("password", this.password);
    record.put("email", this.email);
    record.put("url", this.url);
    record.put("notes", this.notes);
    record.put("changes", changes);

    if (this.records == null) {
      this.records = new HashMap<>();
    }

    this.records.put(CustomDateUtils.prettyDate(LocalDateTime.now()), record);
  }

  // endregion
}
