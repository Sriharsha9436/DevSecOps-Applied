
# DevSecOps SAST Demo using SonarCloud and Azure DevOps

## Overview

This project demonstrates how to integrate **SonarCloud** static application security testing (SAST) into an **Azure DevOps CI/CD pipeline**. The goal is to showcase the automated detection of security vulnerabilities, code maintainability issues in a Java project.

---

## Project Highlights

1. **Integration of SonarCloud with Azure DevOps**  
   - The Azure DevOps pipeline is configured to automatically perform SonarCloud analysis whenever code is pushed to the repository.  
   - The pipeline includes tasks for preparing SonarCloud analysis, building the project using Maven, and publishing the **Quality Gate** results back to Azure DevOps.  
   - The **SonarCloud service connection** in Azure DevOps is used for authentication and seamless integration.

2. **Intentional Vulnerable Code for SAST Testing**  
   - The project contains intentionally vulnerable code and outdated dependencies to trigger SAST rules in SonarCloud.  
   - This allows demonstration of **security hotspots**, **vulnerabilities**, and **code quality issues** detected by SonarCloud.  
   - The purpose is to illustrate how DevSecOps pipelines automatically enforce security and quality standards.

3. **Pipeline Enforcement using Quality Gates**  
   - The Azure DevOps YAML pipeline is configured such that if the **SonarCloud Quality Gate fails**, the pipeline build **automatically fails**.  
   - This ensures that no code with unresolved security or quality issues can be merged or deployed without review.  

---

## Changes in Azure DevOps YAML Pipeline

- Added **SonarCloudPrepare** task to configure analysis for the project.  
- Configured **Maven build task** to include `sonar:sonar` goal for SAST analysis.  
- Added **SonarCloudPublish** task to retrieve and enforce Quality Gate results.  
- Pipeline is set to **fail the build** automatically if any Quality Gate condition (coverage, vulnerabilities, code smells, etc.) is not met.  
- Full clone of repository (`fetchDepth: 0`) is used to ensure accurate analysis.  
- Java 17 is installed and used for Maven build and SonarCloud analysis.

---

## Outcome

- Demonstrates **continuous security and quality checks** in the DevOps pipeline.  
- Provides a reproducible example of **automated SAST** in a CI/CD workflow.  
- Shows how **Quality Gates** enforce standards and prevent insecure or low-quality code from progressing.  

---

**Note:** This repository is for learning and demonstration purposes. All vulnerabilities in the code are intentionally added and should not be used in production environments.
