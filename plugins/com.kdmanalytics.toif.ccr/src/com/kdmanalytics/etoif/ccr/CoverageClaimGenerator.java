/*******************************************************************************
 * Copyright (c) 2016 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.etoif.ccr;

import generated.CWECoverageClaimType;
import generated.CWECoverageClaimType.Claims;
import generated.CWECoverageClaimType.Claims.Claim;
import generated.CWECoverageClaimType.Claims.Claim.RuleSet;
import generated.CWECoverageClaimType.Claims.Claim.RuleSet.Rule;
import generated.CWECoverageClaims;
import generated.MatchAccuracyType;
import generated.ObjectFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.eclipse.core.resources.IFile;

import com.kdmanalytics.toif.ui.common.FindingEntry;

/**
 * 
 * Generate the CCR report.
 * 
 * @Author Adam Nunn <adam@kdmanalytics.com>
 * @Date Apr 1, 2012
 *       
 * @author Ken Duck
 *         
 */
public class CoverageClaimGenerator {
  
  /**
   * get the output file.
   * 
   * @param args
   *          the command line args
   * @return
   */
  static File getOutputFileFromArguments(String[] args) {
    return new File(args[args.length - 1]);
  }
  
  /**
   * any vendor-specific rule identifiers, numbers or names that correspond to the associated CWE ID
   * 
   * @param objFactory
   * @param segment
   * @param repository
   * @return
   */
  private static Collection<? extends Rule> getRules(ObjectFactory objFactory, List<FindingEntry> findings) {
    HashMap<String, Rule> ruleSet = new HashMap<String, CWECoverageClaimType.Claims.Claim.RuleSet.Rule>();
    
    for (FindingEntry finding : findings) {
      String ident = finding.getCoverageIdentifier();
      String description = finding.getCoverageDescription();
      
      if (!ruleSet.containsKey(ident)) {
        Rule rule = objFactory.createCWECoverageClaimTypeClaimsClaimRuleSetRule();
        ruleSet.put(ident, rule);
      }
      
      Rule rule = ruleSet.get(ident);
      rule.setRuleID(ident);
      rule.setRuleName(ident);
      rule.setRuleComments(description);
    }
    
    return ruleSet.values();
    
  }
  
  /**
   * make sure that the arguments are correct.
   * 
   * @param args
   *          command line arguments.
   * @return
   */
  static Boolean verifyArguments(String[] args) {
    if (args.length < 2) {
      return false;
    }
    
    return true;
  }
  
  /**
   * start the Coverege generator without using the main method.
   * 
   * @param findings
   *          Root for the findings to use for export.
   * @param out
   *          the output file to write to.
   * @param writeToTsv
   *          whether to write to tsv or xml. TRUE writes to tsv file, FALSE writes to xml.
   */
  public CoverageClaimGenerator(FindingEntry[] findings, File out, boolean writeToTsv) {
    File outputFile = out;
    
    JAXBContext jc = getJaxContext();
    
    CWECoverageClaims claims = parseRepository(jc, findings);
    
    // write to TSV or marshall to XML.
    if (writeToTsv) {
      writeToTSV(claims, outputFile);
    } else {
      marshalClaims(jc, claims, outputFile);
    }
  }
  
  /**
   * claims are groups of claim. a claim is essentially a cwe.
   * 
   * @param objFactory
   * @param segment
   * @param repository
   * @return
   */
  private Collection<Claim> createClaims(ObjectFactory objFactory, List<FindingEntry> findings) {
    HashMap<String, Claim> claims = new HashMap<String, CWECoverageClaimType.Claims.Claim>();
    
    // Group findings by CWE
    Map<String, List<FindingEntry>> findingsByCwe = new HashMap<String, List<FindingEntry>>();
    for (FindingEntry finding : findings) {
      String cwe = finding.getCwe();
      if (!findingsByCwe.containsKey(cwe)) findingsByCwe.put(cwe, new LinkedList<FindingEntry>());
      List<FindingEntry> entries = findingsByCwe.get(cwe);
      entries.add(finding);
    }
    
    for (Map.Entry<String, List<FindingEntry>> entry : findingsByCwe.entrySet()) {
      String cweIdString = entry.getKey();
      List<FindingEntry> cweFindings = entry.getValue();
      
      if (!claims.containsKey(cweIdString)) {
        Claim claim = objFactory.createCWECoverageClaimTypeClaimsClaim();
        String cweId = cweIdString.replace("CWE-", "");
        cweId = cweIdString.replace("CWE", "");
        claim.setCWEID(cweId);
        // use this to set any additional comments.
        claim.setCWEClaimComments("");
        
        String cweName = getCweName(cweId);
        
        claim.setCWEName(cweName);
        claim.setMatchAccuracy(MatchAccuracyType.UNKNOWN);
        
        claims.put(cweIdString, claim);
      }
      
      Claim claim = claims.get(cweIdString);
      RuleSet ruleSet = objFactory.createCWECoverageClaimTypeClaimsClaimRuleSet();
      ruleSet.getRule().addAll(getRules(objFactory, cweFindings));
      claim.setRuleSet(ruleSet);
    }
    
    return claims.values();
  }
  
  /**
   * coverage claim are a group of claims from a tool
   * 
   * @param objFactory
   * @param repository
   * @return
   */
  private List<CWECoverageClaimType> createCoverageClaims(ObjectFactory objFactory, FindingEntry[] findings) {
    List<CWECoverageClaimType> results = new ArrayList<CWECoverageClaimType>();
    
    // Group findings by tool
    Map<String, List<FindingEntry>> findingsByTool = new HashMap<String, List<FindingEntry>>();
    for (FindingEntry finding : findings) {
      String tool = finding.getTool();
      if (!findingsByTool.containsKey(tool)) findingsByTool.put(tool, new LinkedList<FindingEntry>());
      List<FindingEntry> entries = findingsByTool.get(tool);
      entries.add(finding);
    }
    
    for (Map.Entry<String, List<FindingEntry>> entry : findingsByTool.entrySet()) {
      String tool = entry.getKey();
      List<FindingEntry> toolFindings = entry.getValue();
      
      if (!toolFindings.isEmpty()) {
        CWECoverageClaimType coverageClaim = objFactory.createCWECoverageClaimType();
        
        coverageClaim.setVendorName("KDM Analytics Inc");
        coverageClaim.setToolsetName(tool);
        // FIXME: We need to collect tool version
        // coverageClaim.setToolsetVersion(version.stringValue());
        
        FindingEntry finding = toolFindings.get(0);
        IFile file = finding.getFile();
        String fileName = file.getName();
        
        String langType = getLanguageType(fileName);
        coverageClaim.setLanguageType(langType);
        
        String lang = getLanguage(fileName);
        coverageClaim.setLanguage(lang);
        
        // FIXME: We need to collect a date somewhere
        // XMLGregorianCalendar date = getDate(segment, repository);
        // coverageClaim.setDateOfClaim(date);
        
        Claims claims = objFactory.createCWECoverageClaimTypeClaims();
        Collection<Claim> claimList = createClaims(objFactory, toolFindings);
        
        claims.getClaim().addAll(claimList);
        coverageClaim.setClaims(claims);
        
        results.add(coverageClaim);
      }
    }
    return results;
  }
  
  /**
   * get the cwe name from the id.
   * 
   * @param cweId
   * @return
   */
  private String getCweName(String cweId) {
    CweToName cweToNameUtil = new CweToName();
    return cweToNameUtil.getCweName(cweId);
  }
  
  /**
   * get the jaxb context.
   * 
   * @return
   * 
   */
  private JAXBContext getJaxContext() {
    JAXBContext jc = null;
    
    try {
      jc = JAXBContext.newInstance("generated");
    } catch (JAXBException e) {
      e.printStackTrace();
    }
    return jc;
  }
  
  /**
   * get the language of the file.
   * 
   * @param fileName
   *          the file to get the language from.
   * @return
   */
  private String getLanguage(String fileName) {
    if (fileName.endsWith(".C")) {
      return "C";
    } else if (fileName.endsWith(".Java")) {
      return "Java";
    } else if (fileName.endsWith(".CPP")) {
      return "C++";
    } else if (fileName.endsWith(".Class")) {
      return "Java";
    }
    if (fileName.endsWith(".c")) {
      return "C";
    } else if (fileName.endsWith(".java")) {
      return "Java";
    } else if (fileName.endsWith(".cpp")) {
      return "C++";
    } else if (fileName.endsWith(".class")) {
      return "Java";
    } else if (fileName.endsWith(".h")) {
      return "C";
    } else {
      return "??";
    }
  }
  
  /**
   * get the language type from the file name.
   * 
   * @param stringValue
   *          the language type
   * @return
   */
  private String getLanguageType(String fileName) {
    if (fileName.endsWith(".c")) {
      return "Source Code";
    } else if (fileName.endsWith(".java")) {
      return "Source Code";
    } else if (fileName.endsWith(".cpp")) {
      return "Source Code";
    } else if (fileName.endsWith(".class")) {
      return "Byte Code";
    }
    if (fileName.endsWith(".C")) {
      return "Source Code";
    } else if (fileName.endsWith(".Java")) {
      return "Source Code";
    } else if (fileName.endsWith(".CPP")) {
      return "Source Code";
    } else if (fileName.endsWith(".Class")) {
      return "Byte Code";
    } else if (fileName.endsWith(".h")) {
      return "Source Code";
    } else {
      return "??";
    }
  }
  
  /**
   * marshall the claims to the output.
   * 
   * @param jc
   *          jaxc ontext
   * @param claims
   *          claims to output
   * @param outputFile
   *          the file to write to.
   */
  private void marshalClaims(JAXBContext jc, CWECoverageClaims claims, File outputFile) {
    
    try {
      Marshaller m = jc.createMarshaller();
      m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      m.marshal(claims, outputFile);
    } catch (JAXBException e) {
      e.printStackTrace();
    }
    
  }
  
  /**
   * parse the repository
   * 
   * @param jc
   *          jax context
   * @param repository
   *          the repository to parse.
   * @return
   */
  private CWECoverageClaims parseRepository(JAXBContext jc, FindingEntry[] findings) {
    
    ObjectFactory objFactory = new ObjectFactory();
    
    // root element of the
    CWECoverageClaims CWECoverageClaimRoot = objFactory.createCWECoverageClaims();
    
    List<CWECoverageClaimType> claims = CWECoverageClaimRoot.getCWECoverageClaim();
    
    List<CWECoverageClaimType> coverageClaims = createCoverageClaims(objFactory, findings);
    
    claims.addAll(coverageClaims);
    
    return CWECoverageClaimRoot;
    
  }
  
  /**
   * write each individual claim.
   * 
   * @param writer
   *          the writer to output
   * @param cweCoverageClaimType
   *          the claim type.
   * @throws IOException
   */
  private void writeIndividualClaimsToWriter(FileWriter writer, CWECoverageClaimType cweCoverageClaimType)
      throws IOException {
    List<Claim> individualClaims = cweCoverageClaimType.getClaims().getClaim();
    
    for (Claim claim : individualClaims) {
      writer.append("\tCWE ID: " + claim.getCWEID());
      writer.append("\n");
      writer.append("\tCWE Name: " + claim.getCWEName());
      writer.append("\n");
      writer.append("\tMatch Accuracy: " + claim.getMatchAccuracy());
      writer.append("\n");
      
      writeRuleSetDetailToTSV(writer, claim);
    }
  }
  
  /**
   * write the cwe-coverage-claim to output
   * 
   * @param writer
   *          the output writer
   * @param overallClaims
   *          the cwe-coverage-claim.
   * @throws IOException
   */
  private void writeOverallCoverageClaimsToWriter(FileWriter writer, List<CWECoverageClaimType> overallClaims)
      throws IOException {
    for (CWECoverageClaimType cweCoverageClaimType : overallClaims) {
      writer.append("CWE Version: " + cweCoverageClaimType.getCWEVersion());
      writer.append('\n');
      writer.append("Vendor Name: " + cweCoverageClaimType.getVendorName());
      writer.append('\n');
      writer.append("Toolset Name: " + cweCoverageClaimType.getToolsetName());
      writer.append('\n');
      writer.append("Toolset Version: " + cweCoverageClaimType.getToolsetVersion());
      writer.append('\n');
      writer.append("Claim Date: " + cweCoverageClaimType.getDateOfClaim());
      writer.append('\n');
      writer.append("Language Type: " + cweCoverageClaimType.getLanguageType());
      writer.append('\n');
      writer.append("Language: " + cweCoverageClaimType.getLanguage());
      writer.append('\n');
      
      writeIndividualClaimsToWriter(writer, cweCoverageClaimType);
    }
  }
  
  /**
   * write the rule set to file
   * 
   * @param writer
   *          the output writer
   * @param claim
   *          the claim
   * @throws IOException
   */
  private void writeRuleSetDetailToTSV(FileWriter writer, Claim claim) throws IOException {
    List<Rule> rules = claim.getRuleSet().getRule();
    
    for (Rule rule : rules) {
      writer.append("\t\tRule ID: " + rule.getRuleID());
      writer.append("\n");
      writer.append("\t\tRule Name: " + rule.getRuleName());
      writer.append("\n");
      writer.append("\t\tRule Comments: " + rule.getRuleComments());
      writer.append("\n");
      
      writer.append("\n\n");
    }
    
  }
  
  /**
   * write to tsv.
   * 
   * @param claims
   * @param outputFile
   */
  private void writeToTSV(CWECoverageClaims claims, File outputFile) {
    FileWriter writer = null;
    
    try {
      writer = new FileWriter(outputFile);
      
      // write the header.
      writer.append("Overall Coverage Claim");
      writer.append('\t');
      writer.append("Individual CWE Claims");
      writer.append('\t');
      writer.append("Rule Set Detail");
      writer.append('\n');
      
      List<CWECoverageClaimType> overallClaims = claims.getCWECoverageClaim();
      
      writeOverallCoverageClaimsToWriter(writer, overallClaims);
      
      writer.flush();
      writer.close();
      
    } catch (IOException e) {
      System.err.println("There was an exit whilst writing to the TSV file.");
      System.exit(1);
    }
    
  }
  
}
