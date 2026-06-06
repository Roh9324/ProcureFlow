package com.example.PrcureflowBackend.user;

import java.time.LocalDateTime;

import com.example.PrcureflowBackend.department.Department;
import com.example.PrcureflowBackend.role.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="users")
public class User {
@Id
@GeneratedValue(strategy=GenerationType.IDENTITY)
@Column(nullable=false)
private int id;
@Column(nullable=false)
private String name;
@Column(nullable=false)
private String email;
@ManyToOne
@JoinColumn(name="role_id")
private Role role;
@Column(nullable = false)
private boolean emailVerified = false;
	
public boolean isEmailVerified() {
	return emailVerified;
}
@Column(nullable = false)
private String password;
public String getPassword() {
	return password;
}
public void setPassword(String password) {
	this.password = password;
}
public void setEmailVerified(boolean emailVerified) {
	this.emailVerified = emailVerified;
}
public int getId() {
	return id;
}
public void setId(int id) {
	this.id = id;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public String getEmail() {
	return email;
}
public void setEmail(String email) {
	this.email = email;
}
public Role getRole() {
	return role;
}
public void setRole(Role role) {
	this.role = role;
}
public Department getDepartment() {
	return department;
}
public void setDepartment(Department department) {
	this.department = department;
}
public boolean isActive() {
	return active;
}
public void setActive(boolean active) {
	this.active = active;
}
public LocalDateTime getCreatedAt() {
	return createdAt;
}
public void setCreatedAt(LocalDateTime createdAt) {
	this.createdAt = createdAt;
}
public LocalDateTime getUpdatedAt() {
	return updatedAt;
}
public void setUpdatedAt(LocalDateTime updatedAt) {
	this.updatedAt = updatedAt;
}
@ManyToOne
@JoinColumn(name="department_id")
private Department department;
@Column(nullable=false)
private boolean active=true;

LocalDateTime createdAt;
LocalDateTime updatedAt;
	
}
