/*******************************************************************************
 * Copyright (c) 2015 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.ui.internal.filters;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * create the dialog for the filters.
 * 
 * @author Adam Nunn <adam@kdmanalytics.com>
 * @author Ken Duck
 *         
 */
public class FiltersDialog extends Dialog {
  
  private IsValidFilter isValidFilter = null;
  
  private NotValidFilter notValidFilter = null;
  
  private InvalidSfpFilter invalidSfpFilter = null;
  
  private TwoToolsFilter twoToolsFilter = null;
  
  private CWETwoToolsFilter CWETwoToolsFilter = null;
  
  private SFPTwoToolsFilter SFPTwoToolsFilter = null;
  
  private TrustFilter trustFilter = null;
  
  private final List<ViewerFilter> filters;
  
  private Text trustAmountText;
  
  private int trustAmount = 0;
  
  /**
   * create the new filter dialog.
   * 
   * @param parentShell
   *          the parent shell for this dialog.
   * @param filters
   *          the applied filters.
   */
  public FiltersDialog(Shell parentShell, ViewerFilter[] filters) {
    super(parentShell);
    this.filters = new ArrayList<ViewerFilter>();
    applyFilters(filters);
  }
  
  /**
   * Set the check boxes and internal cache for all current filters
   * 
   * @param filters
   */
  private void applyFilters(ViewerFilter[] filters) {
    for (ViewerFilter viewerFilter : filters) {
      this.filters.add(viewerFilter);
      
      if (viewerFilter instanceof IsValidFilter) {
        isValidFilter = (IsValidFilter) viewerFilter;
      }
      if (viewerFilter instanceof NotValidFilter) {
        notValidFilter = (NotValidFilter) viewerFilter;
      }
      if (viewerFilter instanceof TrustFilter) {
        trustFilter = (TrustFilter) viewerFilter;
      }
      if (viewerFilter instanceof TwoToolsFilter) {
        twoToolsFilter = (TwoToolsFilter) viewerFilter;
      }
      if (viewerFilter instanceof CWETwoToolsFilter) {
        CWETwoToolsFilter = (CWETwoToolsFilter) viewerFilter;
      }
      if (viewerFilter instanceof SFPTwoToolsFilter) {
        SFPTwoToolsFilter = (SFPTwoToolsFilter) viewerFilter;
      }
      if (viewerFilter instanceof InvalidSfpFilter) {
        invalidSfpFilter = (InvalidSfpFilter) viewerFilter;
      }
      if (viewerFilter instanceof AndFilter) {
        applyFilters(((AndFilter) viewerFilter).getFilters());
      }
    }
  }
  
  /**
   * creat the checkboxes
   * 
   * @param checkboxComposite
   *          the composite that the checkboxes belong in.
   */
  private void createCheckbox(Composite checkboxComposite) {
    // filter one.
    Composite comp1 = new Composite(checkboxComposite, SWT.NONE);
    comp1.setLayout(new GridLayout());
    final Button sameLocButton = new Button(comp1, SWT.CHECK);
    if (twoToolsFilter != null) {
      sameLocButton.setSelection(true);
    }
    sameLocButton.setText("2+ Tools report same location.");
    sameLocButton.addSelectionListener(new SelectionListener() {
      
      @Override
      public void widgetDefaultSelected(SelectionEvent e) {
        // TODO Auto-generated method stub
        
      }
      
      @Override
      public void widgetSelected(SelectionEvent e) {
        if (sameLocButton.getSelection()) {
          twoToolsFilter = new TwoToolsFilter();
          filters.add(twoToolsFilter);
        } else {
          twoToolsFilter = null;
        }
      }
    });
    
    // filter two.
    Composite comp2 = new Composite(checkboxComposite, SWT.NONE);
    comp2.setLayout(new GridLayout());
    final Button sameCWEButton = new Button(comp2, SWT.CHECK);
    if (CWETwoToolsFilter != null) {
      sameCWEButton.setSelection(true);
    }
    sameCWEButton.setText("2+ Tools report same location. With the same CWE");
    sameCWEButton.addSelectionListener(new SelectionListener() {
      
      @Override
      public void widgetDefaultSelected(SelectionEvent e) {
        // TODO Auto-generated method stub
        
      }
      
      @Override
      public void widgetSelected(SelectionEvent e) {
        if (sameCWEButton.getSelection()) {
          CWETwoToolsFilter = new CWETwoToolsFilter();
          filters.add(CWETwoToolsFilter);
        } else {
          CWETwoToolsFilter = null;
        }
      }
    });
    
    // filter 3.
    Composite compSFPTwoTools = new Composite(checkboxComposite, SWT.NONE);
    compSFPTwoTools.setLayout(new GridLayout());
    final Button samesfpButton = new Button(compSFPTwoTools, SWT.CHECK);
    if (SFPTwoToolsFilter != null) {
      samesfpButton.setSelection(true);
    }
    samesfpButton.setText("2+ Tools report same location. With the same SFP");
    samesfpButton.addSelectionListener(new SelectionListener() {
      
      @Override
      public void widgetDefaultSelected(SelectionEvent e) {
        // TODO Auto-generated method stub
        
      }
      
      @Override
      public void widgetSelected(SelectionEvent e) {
        if (samesfpButton.getSelection()) {
          SFPTwoToolsFilter = new SFPTwoToolsFilter();
          filters.add(SFPTwoToolsFilter);
        } else {
          SFPTwoToolsFilter = null;
        }
      }
    });
    
    // filter 4. filter goes in its own composite to allow a text field as
    // well.
    Composite trustComposite = new Composite(checkboxComposite, SWT.NONE);
    trustComposite.setLayout(new GridLayout(2, true));
    
    // button.
    final Button trustButton = new Button(trustComposite, SWT.CHECK);
    trustButton.setText("Trust above:");
    
    GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1);
    
    trustAmountText = new Text(trustComposite, SWT.SINGLE | SWT.BORDER | SWT.FILL);
    trustAmountText.setLayoutData(gridData);
    if (trustFilter != null) {
      trustButton.setSelection(true);
      trustAmountText.setText(String.valueOf(trustFilter.getAmount()));
    } else {
      trustAmountText.setText("50");
    }
    trustAmountText.setTextLimit(6);
    
    // this is a verify listener. it makes sure that the characters punched
    // into the text box are correct.
    trustAmountText.addListener(SWT.Verify, new Listener() {
      
      @Override
      public void handleEvent(Event event) {
        String input = event.text;
        char[] characters = new char[input.length()];
        input.getChars(0, characters.length, characters, 0);
        for (int i = 0; i < characters.length; i++) {
          if (!('0' <= characters[i] && characters[i] <= '9')) {
            event.doit = false;
            return;
          }
        }
      }
      
    });
    
    // add the selection listener to the button.
    trustButton.addSelectionListener(new TrustSelectionListener(trustAmountText) {
      
      @Override
      public void widgetDefaultSelected(SelectionEvent e) {
      }
      
      @Override
      public void widgetSelected(SelectionEvent e) {
        if (trustButton.getSelection()) {
          String text = trustAmountText.getText();
          try {
            int amount = Integer.parseInt(text);
            trustFilter = new TrustFilter(amount);
          } catch (Exception e2) {
            System.err.println("Trust filter not added. Text field could not be parsed as integer.");
          }
          
        } else {
          trustFilter = null;
        }
        
      }
    });
    
    // filter 5.
    Composite isValidComp = new Composite(checkboxComposite, SWT.NONE);
    isValidComp.setLayout(new GridLayout());
    final Button isValidButton = new Button(isValidComp, SWT.CHECK);
    if (isValidFilter != null) {
      isValidButton.setSelection(true);
    }
    isValidButton.setText("Is Valid.");
    isValidButton.addSelectionListener(new SelectionListener() {
      
      @Override
      public void widgetDefaultSelected(SelectionEvent e) {
      }
      
      @Override
      public void widgetSelected(SelectionEvent e) {
        if (isValidButton.getSelection()) {
          isValidFilter = new IsValidFilter();
          filters.add(isValidFilter);
        } else {
          isValidFilter = null;
        }
      }
    });
    
    // filter 6.
    Composite notValidComp = new Composite(checkboxComposite, SWT.NONE);
    notValidComp.setLayout(new GridLayout());
    final Button notValidButton = new Button(notValidComp, SWT.CHECK);
    if (notValidFilter != null) {
      notValidButton.setSelection(true);
    }
    notValidButton.setText("Not Valid.");
    notValidButton.addSelectionListener(new SelectionListener() {
      
      @Override
      public void widgetDefaultSelected(SelectionEvent e) {
      }
      
      @Override
      public void widgetSelected(SelectionEvent e) {
        if (notValidButton.getSelection()) {
          notValidFilter = new NotValidFilter();
          filters.add(notValidFilter);
        } else {
          notValidFilter = null;
        }
      }
    });
    
    // filter 7.
    Composite invalidSfpComp = new Composite(checkboxComposite, SWT.NONE);
    invalidSfpComp.setLayout(new GridLayout());
    final Button invalidSfpButton = new Button(invalidSfpComp, SWT.CHECK);
    if (invalidSfpFilter != null) {
      invalidSfpButton.setSelection(true);
    }
    invalidSfpButton.setText("Not SFP--1");
    invalidSfpButton.addSelectionListener(new SelectionListener() {
      
      @Override
      public void widgetDefaultSelected(SelectionEvent e) {
      }
      
      @Override
      public void widgetSelected(SelectionEvent e) {
        if (invalidSfpButton.getSelection()) {
          invalidSfpFilter = new InvalidSfpFilter();
          filters.add(invalidSfpFilter);
        } else {
          invalidSfpFilter = null;
        }
      }
    });
    
  }
  
  /**
   * 
   */
  @Override
  protected Control createDialogArea(Composite parent) {
    Composite container = (Composite) super.createDialogArea(parent);
    
    GridData gridData_1 = new GridData(SWT.LEFT, GridData.FILL, true, false, 2, 1);
    
    Label label = new Label(container, SWT.NONE);
    
    label.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 2, 1));
    
    label.setText("Select the filters which will act upon the data:     ");
    
    Composite checkboxComposite = new Composite(container, SWT.NONE);
    
    checkboxComposite.setLayoutData(gridData_1);
    
    GridLayout checkboxLayout = new GridLayout(1, false);
    
    checkboxComposite.setLayout(checkboxLayout);
    
    // create the check boxes
    createCheckbox(checkboxComposite);
    
    return container;
    
  }
  
  /**
   * get all the filters
   * 
   * @return a list of the filters.
   */
  public List<ViewerFilter> getFilters() {
    return filters;
    
  }
  
  /**
   * get the trust amount
   * 
   * @return the trustAmount
   */
  public int getTrustAmount() {
    return trustAmount;
  }
  
  /**
   * @return the trustFilter
   */
  public TrustFilter getTrustFilter() {
    return trustFilter;
  }
  
  /**
   * @return the twoToolsFilter
   */
  public TwoToolsFilter getTwoToolsFilter() {
    return twoToolsFilter;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.dialogs.Dialog#okPressed()
   */
  @Override
  protected void okPressed() {
    save();
    super.okPressed();
  }
  
  /**
   * store the trust amount
   */
  private void save() {
    try {
      trustAmount = Integer.parseInt(trustAmountText.getText());
    } catch (NumberFormatException e) {
    
    }
    
  }
  
  /**
   * get teh filter for the sma cwe filter
   * 
   * @return
   */
  public CWETwoToolsFilter getCWETwoToolsFilter() {
    return CWETwoToolsFilter;
  }
  
  /**
   * get the filter for the same sfp filter
   * 
   * @return
   */
  public SFPTwoToolsFilter getSFPTwoToolsFilter() {
    return SFPTwoToolsFilter;
  }
  
  /**
   * get the is valid filter
   * 
   * @return
   */
  public IsValidFilter getIsValidFilter() {
    return isValidFilter;
  }
  
  /**
   * get the not valid filter.
   * 
   * @return
   */
  public NotValidFilter getNotValidFilter() {
    return notValidFilter;
  }
  
  /**
   * get the invalid sfp filter.
   * 
   * @return
   */
  public InvalidSfpFilter getInvalidSfpFilter() {
    return invalidSfpFilter;
  }
  
}
