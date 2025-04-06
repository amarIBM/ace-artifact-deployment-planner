"use client"
import React, { useState, useEffect } from 'react';
import { Grid, Column, Button, FileUploader } from "@carbon/react";
import Image from 'next/image';
import logo from '@/app/ibm-logo.png';

function App() {
  const [hoveredTable, setHoveredTable] = useState(null);
  const [data, setData] = useState({});
  const [selectedCategory, setSelectedCategory] = useState(null);
  const [selectedGroup, setSelectedGroup] = useState(null);
  const [selectedCheckboxes, setSelectedCheckboxes] = useState([]);
  const [displayResults, setDisplayResults] = useState(false);
  const [totalApps, setTotalApps] = useState(0);
  const [mainCategories, setMainCategories] = useState([]);
  const [openCategories, setOpenCategories] = useState({});
  const [showLemTables, setShowLemTables] = useState(false);
  const [text, setText] = useState('');
  const [fileType, setFileType] = useState('');
  const [fileList, setFileList] = useState([]);
  const [listApp,setListApp]= useState([])
  const [inputFolder, setInputFolder] = useState('');
  const [inputCount, setInputCount] = useState(null);
  const [isClient, setClient] = useState(false);
  const [operationType, setOperationType] = useState('AND');
  const [lemData, setLemData] = useState({
    JavaNodes: {},
    JavaScriptNodes: {},
    ResourceManagers: {}
  });

  const groups = {
    Transport: ["MQ", "Http", "TCPIP", "File", "Email", "SOAP", "REST"],
    Aggregate: ["Aggregation", "Group"],
    Database: ["JDBC", "Database_ODBC"],
    SSL: ["SSL"],
  };

  useEffect(() => {
    // Fetch data from the API
    setClient(true);
    const fetchData = async () => {
      try {
        const response = await fetch('/api/read-file');
        if (!response.ok) throw new Error('Network response was not ok');
        const text = await response.text();
        parseFileData(text);
      } catch (error) {
        console.error('Error fetching file:', error);
      }
    };

    fetchData();
  }, []);

  const parseFileData = (text, fileName = '') => {
    const parsedData = {};
    const detectedMainCategories = [];
    let appCount = 0;
    const newLemData = {
      JavaNodes: {},
      JavaScriptNodes: {},
      ResourceManagers: {}
    };

    const lines = text.split("\n").map(line => line.trim()).filter(line => line !== "");

    if (lines.length === 0) {
      console.error("File is empty or not formatted correctly.");
      return;
    }
    
    const typeMatch = lines[0]?.match(/Type:\s*(\w+)/);
    const inputMatch = lines[1]?.match(/Input\s*:\s*(\S+)/);
    const filesMatch = lines[2]?.match(/List of (?:bar files|servers):\s*\[(.*?)\]/i);
    const appMatch=lines[3]?.match(/List of apps: \s*\[(.*?)\]/i);
    const countMatch = lines[4]?.match(/Count:\s*(\d+)/);

    if (typeMatch) {
      setFileType(typeMatch[1]);
    }
    if (inputMatch) {
      setInputFolder(inputMatch[1]);
    }
    if (filesMatch) {
      setFileList(filesMatch[1].split(",").map(file => file.trim()));
    }
    if (appMatch) {
      const apps = appMatch[1].split(",").map(app => app.trim());
    
      const type = typeMatch[1] === "Bar" ? "bar" : "zip"; 
      const appMap = {};
    
      apps.forEach(app => {
        let [base, appName] = app.split('|').map(s => s.trim());
        if (!appMap[base]) {
          appMap[base] = [];
        }
        appMap[base].push(appName);
      });
    
      const formattedList = Object.entries(appMap).map(([file, appNames]) => ({
        file: type === "zip" ? file : `${file}.${type}`,
        applications: appNames.join(", ")
      }));
    
      setListApp(formattedList);
    }
    if (countMatch) {
      setInputCount(parseInt(countMatch[1], 10));
    }

    lines.slice(5).forEach((line) => {
        if (line.trim()) {
          const [key, values] = line.split(":");
          
          // Extract applications from the line
          const match = values.match(/\[(.*?)\]/);
          const apps = match && match[1].trim() !== "" ? match[1].split(",").map((app) => app.trim()) : [];
          
          if (key.includes("_")) {
            // Handle categories with underscores (e.g., Nodes_Database_ODBC)
            const parts = key.split("_");
            const mainCategory = parts[0];
            const subCategory = parts.slice(1).join("_");
            
            // Handle special categories (JavaNodes, JavaScriptNodes, ResourceManagers)
            if (["JavaNodes", "JavaScriptNodes", "ResourceManagers"].includes(mainCategory)) {
              if (!newLemData[mainCategory]) {
                newLemData[mainCategory] = {};
              }
              newLemData[mainCategory][parts[1]] = apps;
            }
            
            // Add to the main category structure
            if (!parsedData[mainCategory]) {
              parsedData[mainCategory] = {};
            }
            parsedData[mainCategory][subCategory] = apps;
            
            // For each part of the subcategory, add it to the flat structure for group display
            const lastPart = parts[parts.length - 1];
            if (!parsedData[lastPart]) {
              parsedData[lastPart] = [];
            }
            parsedData[lastPart].push(...apps);
            
            // For compound categories like Database_ODBC, add to the flat structure as well
            if (parts.length > 2) {
              const compoundCategory = parts.slice(1).join("_");
              if (!parsedData[compoundCategory]) {
                parsedData[compoundCategory] = [];
              }
              parsedData[compoundCategory].push(...apps);
            }
            
          } else {
            // Handle simple categories without underscores
            const category = key.trim();
            parsedData[category] = apps;
            detectedMainCategories.push(category);
          }
        }
      });
    
    
    setLemData(newLemData);
    setMainCategories(detectedMainCategories);
    setData(parsedData);
  };

  const handleFileUpload = (event) => {
    if (!isClient) return;
    const file = event.target.files[0];
    if (file) {
      setData({});
      setMainCategories([]);
      setTotalApps(0);
      setFileType('');
      setFileList([]);
      setListApp([]);
      setInputFolder('');
      setSelectedCheckboxes([]);
      setDisplayResults(false);
      setSelectedGroup(null);
      setShowLemTables(false);

      const reader = new FileReader();
      reader.onload = (e) => {
        const text = e.target.result;
        parseFileData(text, file.name);
      };
      reader.readAsText(file);
    }
  };

  const handleCheckboxChange = (checked, category, subCategory = null) => {
    setSelectedGroup(null);
    setShowLemTables(false);
    
    setSelectedCheckboxes(prev => {
      if (checked) {
        return [...prev, { category, subCategory }];
      } else {
        return prev.filter(item => 
          !(item.category === category && item.subCategory === subCategory)
        );
      }
    });
    setDisplayResults(true);
  };

  const getCommonApps = () => {
    let allSets = [];

    selectedCheckboxes.forEach(({ category, subCategory }) => {
      if (subCategory) {
        if (data[category]?.[subCategory]) {
          allSets.push(new Set(data[category][subCategory]));
        }
      } else {
        if (Array.isArray(data[category])) {
          allSets.push(new Set(data[category]));
        }
      }
    });

    if (allSets.length === 0) return [];
    if (allSets.length === 1) return Array.from(allSets[0]);

    if (operationType === 'AND') {
      // Intersection (AND) operation
      return Array.from(allSets.reduce((acc, curr) => {
        return new Set([...acc].filter(x => curr.has(x)));
      }));
    } else {
      // Union (OR) operation
      return Array.from(allSets.reduce((acc, curr) => {
        return new Set([...acc, ...curr]);
      }));
    }
  };

  const handleGroupSelection = (group) => {
    setSelectedGroup(group);
    setSelectedCategory(null);
    setSelectedCheckboxes([]);
    setShowLemTables(false);
    setDisplayResults(false);
    setOpenCategories({});
  };

  const handleCategoryChange = (category) => {
    setOpenCategories(prev => ({
      ...prev,
      [category]: !prev[category]
    }));
  };

  const handleClearSelection = () => {
    setSelectedCheckboxes([]);
    setDisplayResults(false);
  };

  const getGroupCategories = (group) => {
    return groups[group] || [];
  };

  const getCategoryCount = (category, subCategory = null) => {
    if (!data[category]) return 0;
  
    if (subCategory) {
      // Handle subcategory count
      if (data[category][subCategory]) {
        return Array.isArray(data[category][subCategory]) ? data[category][subCategory].length : 0;
      }
      return 0;
    }
  
    // Handle main category count
    if (Array.isArray(data[category])) {
      return data[category].length;
    }
  
    // Handle object with subcategories
    if (typeof data[category] === 'object') {
      return Object.values(data[category]).reduce((sum, apps) => 
        sum + (Array.isArray(apps) ? apps.length : 0), 0
      );
    }
  
    return 0;

  };

  const createAppComponentsMapping = () => {
    const mapping = {};

    // Helper function to process category and apps
    const processCategory = (category, apps, subCategory = null) => {
        let categoryName = subCategory ? `${category}_${subCategory}` : category;

        // Extract the last part after the last '_', or keep as is
        categoryName = categoryName.includes('_') ? categoryName.split('_').pop() : categoryName;

        apps.forEach(app => {
            const [file, appName] = app.split('|').map(s => s.trim());
            if (!mapping[appName]) {
                mapping[appName] = new Set();
            }
            mapping[appName].add(categoryName);
        });
    };

    // Process all categories and their apps
    Object.entries(data).forEach(([category, value]) => {
        if (Array.isArray(value)) {
            processCategory(category, value);
        } else if (typeof value === 'object') {
            Object.entries(value).forEach(([subCategory, apps]) => {
                processCategory(category, apps, subCategory);
            });
        }
    });

    // Convert to array of objects for table display
    return Object.entries(mapping).map(([app, components]) => ({
        app,
        components: Array.from(components).join(', ')
    }));
};

const formatAppDisplay = (app) => {
  const [file, appName] = app.split('|').map(s => s.trim());
  const fileExt = fileType.toLowerCase() === 'zip' ? '' : '.bar'; 
  return `${appName} (${file}${fileExt})`;
};


  const TableWrapper = ({ children, id, title }) => {
    const [isHovered, setIsHovered] = useState(false);
    const isExpanded = hoveredTable === id;
    const tableRef = React.useRef(null);
    const [showArrow, setShowArrow] = useState(false);

    React.useEffect(() => {
      if (tableRef.current) {
        const tbody = tableRef.current.querySelector('tbody');
        setShowArrow(tbody.scrollHeight > 168);
      }
    }, [children]);
    
    return (
      <div 
        style={{ 
          width: "calc(33% - 1.33rem)",
          marginBottom: "2rem",
          position: "relative",
          zIndex: isHovered ? 10 : 1,
        }}
        onMouseEnter={() => {
          setIsHovered(true);
          setHoveredTable(id);
        }}
        onMouseLeave={() => {
          setIsHovered(false);
          setHoveredTable(null);
        }}
      >
        <div style={{
          position: "relative",
          height: isExpanded ? "auto" : "200px",
          overflow: "hidden",
          transition: "all 0.3s ease",
          backgroundColor: "white",
          borderRadius: "8px",
          boxShadow: isHovered ? "0 10px 25px rgba(0,0,0,0.1)" : "none",
        }}>
          <div ref={tableRef}>
            {children}
          </div>
          {showArrow && (
            <div style={{
              position: "absolute",
              bottom: "0",
              left: "0",
              right: "0",
              height: "40px",
              background: "linear-gradient(transparent, rgba(255, 255, 255, 0.9))",
              display: "flex",
              justifyContent: "center",
              alignItems: "center",
              pointerEvents: "none"
            }}>
              <span style={{
                fontSize: "24px",
                color: "#4a5568",
                transform: isExpanded ? "rotate(180deg)" : "rotate(0deg)",
                transition: "transform 0.3s ease",
                opacity: "0.7"
              }}>
                ▼
              </span>
            </div>
          )}
        </div>
      </div>
    );
  };

  const renderLemTable = (title, data) => {
    const apps = Object.values(data).flat().filter(app => app);
    if (apps.length === 0) return null;
    
    return (
      <TableWrapper id={title} title={title}>
        <table style={{ 
          width: "100%", 
          borderCollapse: "separate",
          borderSpacing: "0",
          border: "1px solid #e2e8f0",
          borderRadius: "8px",
          overflow: "hidden",
          minHeight: "200px"
        }}>
          <thead>
            <tr style={{ backgroundColor: "#f0f0f0" }}>
              <th style={{ 
                padding: "16px", 
                fontSize: "1.125rem", 
                textAlign: "center",
                color: "#2d3748",
                borderBottom: "2px solid #e2e8f0"
              }}>{title}</th>
            </tr>
          </thead>
          <tbody>
            {apps.map((app, index) => (
              <tr key={`${app}-${index}`} style={{
                backgroundColor: index % 2 === 0 ? "white" : "#f8fafc"
              }}>
                <td style={{ 
                  padding: "12px 16px",
                  color: "#4a5568",
                  textAlign: "center",
                  borderBottom: "1px solid #e2e8f0" 
                }}>{formatAppDisplay(app)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </TableWrapper>
    );
  };

  const renderAppComponentsTable = () => {
    const mappedData = createAppComponentsMapping();
    
    return (
      <div style={{ marginTop: "2rem" }}>
      <h2 style={{ 
        color: "#2d3748", 
        marginBottom: "1.5rem",
        fontSize: "1.5rem",
        fontWeight: "600"
      }}>Applications and Components</h2>
      <table style={{ 
  minWidth: "50%",
  borderCollapse: "separate",
  borderSpacing: "0",
  border: "1px solid #e2e8f0", // Outer border with lighter color
  borderRadius: "8px",
  overflow: "hidden",
  minHeight: "100px"
}}>
  <thead>
    <tr style={{ backgroundColor: "#f0f0f0" }}>
      <th style={{ 
        padding: "16px", 
        fontSize: "1.125rem", 
        textAlign: "left",
        color: "#2d3748",
        borderBottom: "2px solid #ccc", // Darker line under header
        borderRight: "1px solid #ccc",  // Darker line between columns
        width: "20%" // Adjusted width for the first column
      }}>
        Applications
      </th>
      <th style={{ 
        padding: "16px", 
        fontSize: "1.125rem", 
        textAlign: "left",
        color: "#2d3748",
        borderBottom: "2px solid #ccc",  // Darker line under header
        width: "80%" // Adjusted width for the second column
      }}>
        Components
      </th>
    </tr>
  </thead>
  <tbody>
    {mappedData.length > 0 ? (
      mappedData.map((row, index) => (
        <tr key={row.app} style={{
          backgroundColor: index % 2 === 0 ? "white" : "#f8fafc"
        }}>
          <td style={{ 
            padding: "12px 16px",
            color: "#4a5568",
            textAlign: "left",
            borderBottom: "1px solid #ccc", // Darker line between rows
            borderRight: "1px solid #ccc"   // Darker line between columns
          }}>
            {row.app}
          </td>
          <td style={{ 
            padding: "12px 16px",
            color: "#4a5568",
            textAlign: "left",
            borderBottom: "1px solid #ccc"  // Darker line between rows
          }}>
            {row.components}
          </td>
        </tr>
      ))
    ) : (
      <tr>
        <td colSpan={2} style={{ 
          padding: "16px",
          color: "#718096",
          textAlign: "center",
          backgroundColor: "white"
        }}>
          No applications found
        </td>
      </tr>
    )}
  </tbody>
</table>
    </div>
    );
  };

  const renderSelectedCategoriesTables = () => {
    return selectedCheckboxes.map(({ category, subCategory }) => {
      const apps = subCategory ? data[category]?.[subCategory] : data[category];
      const title = subCategory ? `${category} - ${subCategory}` : category;
      
      return (
        <TableWrapper key={`${category}-${subCategory}`} id={`${category}-${subCategory}`} title={title}>
          <table style={{ 
            width: "100%",
            borderCollapse: "separate",
            borderSpacing: "0",
            border: "1px solid #e2e8f0",
            borderRadius: "8px",
            overflow: "hidden",
            minHeight: "200px"
          }}>
            <thead>
              <tr style={{ backgroundColor: "#f0f0f0" }}>
                <th style={{ 
                  padding: "16px", 
                  fontSize: "1.125rem", 
                  textAlign: "center",
                  color: "#2d3748",
                  borderBottom: "2px solid #e2e8f0"
                }}>{title}</th>
              </tr>
            </thead>
            <tbody>
              {apps && apps.length > 0 ? (
                apps.map((app, index) => (
                  <tr key={`${app}-${index}`} style={{
                    backgroundColor: index % 2 === 0 ? "white" : "#f8fafc"
                  }}>
                    <td style={{ 
                      padding: "12px 16px",
                      color: "#4a5568",
                      textAlign: "center",
                      borderBottom: "1px solid #e2e8f0" 
                    }}>{formatAppDisplay(app)}</td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td style={{ 
                    padding: "16px",
                    color: "#718096",
                    textAlign: "center",
                    backgroundColor: "white"
                  }}>
                    No applications found under {title}
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </TableWrapper>
      );
    });
  };
  

  return (
    <div style={{ position: 'relative', minHeight: '100vh' }}>
      <div style={{
        background: 'linear-gradient(to right, #2C5282, #1A365D)',
        padding: '1rem',
        color: 'white',
        display: 'flex',
        alignItems: 'center',
        gap: '1rem',
        boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.1)',
      }}>
        <Image 
          src={logo} 
          alt="Logo" 
          width={30} 
          height={30} 
          style={{ objectFit: 'contain' }}
        />
        <h2 style={{
          margin: 0,
          fontSize: '1.25rem',
          fontWeight: '600',
          letterSpacing: '-0.025em',
        }}>IBM App Connect Enterprise</h2>
      </div>

      <Grid fullWidth style={{ height: "100vh", display: "flex", padding: "20px" }}>
        <Column style={{ 
          background: "white",
          padding: "2rem",
          width: "25%",
          borderRadius: "16px",
          boxShadow: "0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06)",
          marginRight: "2rem",
          maxHeight: "calc(100vh - 64px)",
          overflowY: "auto" 
        }}>
          <FileUploader
            accept={[".txt"]}
            buttonKind="primary"
	        labelTitle={<span style={{ fontSize: "18px" }}>Upload optimizer report</span>}
            labelDescription="Only .txt files are supported."
            buttonLabel="Add file"
            filenameStatus="edit"
            iconDescription="Delete file"
            multiple={false}
            id="file-uploader"
            onChange={(event) => handleFileUpload(event)}
          />
          
          <div style={{ marginBottom: "2.5rem" }}> 
            <h3 style={{ 
              fontSize: "1.25rem", 
              fontWeight: "600", 
              marginBottom: "1.5rem",
              marginTop: "2rem",
              color: "#2d3748"
            }}>Group by</h3>
            {Object.keys(groups).map((group) => (
              <Button
                key={group}
                style={{ 
                  marginBottom: "12px", 
                  backgroundColor: selectedGroup === group ? "#0066cc" : "#f8f9fa",
                  color: selectedGroup === group ? "white" : "#2d3748",
                  display: "block", 
                  width: "100%",
                  padding: "12px",
                  borderRadius: "8px",
                  border: "1px solid #e2e8f0",
                  transition: "all 0.2s ease",
                  boxShadow: "0 2px 4px rgba(0,0,0,0.05)",
                  cursor: "pointer",
                  fontWeight: "bold",
		  fontSize: "16px",
                }}
                onClick={() => handleGroupSelection(group)}
              >
                {group}
              </Button>
            ))}
            <Button
              style={{ 
                marginTop: "1rem",
                backgroundColor: showLemTables ? "#0066cc" : "#f8f9fa",
                color: showLemTables ? "white" : "#2d3748",
                display: "block", 
                width: "100%",
                padding: "12px",
                borderRadius: "8px",
                border: "1px solid #e2e8f0",
                transition: "all 0.2s ease",
                boxShadow: "0 2px 4px rgba(0,0,0,0.05)",
                cursor: "pointer",
                fontWeight: "bold",
          	fontSize: "16px",
              }}
              onClick={() => {
                setShowLemTables(!showLemTables);
                setSelectedGroup(null);
                setSelectedCategory(null);
                setSelectedCheckboxes([]);
                setDisplayResults(false);
                setOpenCategories({});
              }}
            >
              Java Based Resources
            </Button>
          </div>

          <div>
            <div style={{ 
              display: "flex", 
              alignItems: "center",
              marginBottom: "1.5rem",
              gap: "0.5rem"
            }}>
              <h3 style={{ 
                fontSize: "1.25rem", 
                fontWeight: "600", 
                marginBottom: "0rem",
                marginRight: "auto",
                color: "#2d3748"
              }}>Categories</h3>
              <div style={{
                display: "flex",
                alignItems: "center",
                gap: "0.5rem"
              }}>
                <div style={{
                  display: "flex",
                  alignItems: "center",
                  background: "#f8f9fa",
                  borderRadius: "6px",
                  padding: "2px",
                  border: "1px solid #e2e8f0"
                }}>
                  <button
                    onClick={() => setOperationType('AND')}
                    style={{
                      padding: "4px 8px",
                      fontSize: "0.75rem",
                      color: operationType === 'AND' ? "white" : "#4a5568",
                      backgroundColor: operationType === 'AND' ? "#0066cc" : "transparent",
                      border: "none",
                      borderRadius: "4px",
                      cursor: "pointer",
                      transition: "all 0.2s ease"
                    }}
                  >
                    Match All
                  </button>
                  <button
                    onClick={() => setOperationType('OR')}
                    style={{
                      padding: "4px 8px",
                      fontSize: "0.75rem",
                      color: operationType === 'OR' ? "white" : "#4a5568",
                      backgroundColor: operationType === 'OR' ? "#0066cc" : "transparent",
                      border: "none",
                      borderRadius: "4px",
                      cursor: "pointer",
                      transition: "all 0.2s ease"
                    }}
                  >
                    Match Any
                  </button>
                </div>
                {selectedCheckboxes.length > 0 && (
                  <button
                    onClick={handleClearSelection}
                    style={{
                      padding: "6px 12px",
                      fontSize: "0.75rem",
                      color: "#4a5568",
                      backgroundColor: "#f8f9fa",
                      border: "1px solid #e2e8f0",
                      borderRadius: "4px",
                      cursor: "pointer",
                      whiteSpace: "nowrap",
                      transition: "all 0.2s ease"
                    }}
                    onMouseOver={(e) => {
                      e.currentTarget.style.backgroundColor = "#edf2f7";
                      e.currentTarget.style.color = "#2d3748";
                    }}
                    onMouseOut={(e) => {
                      e.currentTarget.style.backgroundColor = "#f8f9fa";
                      e.currentTarget.style.color = "#4a5568";
                    }}
                  >
                    Clear All
                  </button>
                )}
              </div>
            </div>
            
            {/* Main Categories */}
            {mainCategories.map((category) => (
              <div key={category} style={{ marginBottom: "1rem" }}>
                <div 
                  onClick={() => handleCategoryChange(category)}
                  style={{ 
                    cursor: "pointer",
                    display: "flex",
                    alignItems: "center",
                    gap: "0.5rem",
                    color: selectedCheckboxes.some(item => item.category === category) ? '#0066cc' : 'inherit'
                  }}
                >
                  <span>{openCategories[category] ? '▼' : '▶'}</span>
                  <strong>{category} ({getCategoryCount(category)})</strong>
                </div>
                {openCategories[category] && (
                  <div style={{ paddingLeft: "1.5rem", marginTop: "0.5rem" }}>
                    <label style={{ display: "flex", alignItems: "center", gap: "0.5rem" }}>
                      <input
                        type="checkbox"
                        onChange={(e) => handleCheckboxChange(e.target.checked, category)}
                        checked={selectedCheckboxes.some(item => 
                          item.category === category && !item.subCategory
                        )}
                      />
                      {category} ({getCategoryCount(category)})
                    </label>
                  </div>
                )}
              </div>
            ))}
            
            {/* Other Categories */}
            {Object.keys(data).map((category) => {
              if (mainCategories.includes(category)) return null;

              if (typeof data[category] === 'object' && !Array.isArray(data[category])) {
                return (
                  <div key={category} style={{ marginBottom: "1rem" }}>
                    <div 
                      onClick={() => handleCategoryChange(category)}
                      style={{ 
                        cursor: "pointer",
                        display: "flex",
                        alignItems: "center",
                        gap: "0.5rem",
                        color: selectedCheckboxes.some(item => item.category === category) ? '#0066cc' : 'inherit'
                      }}
                    >
                      <span>{openCategories[category] ? '▼' : '▶'}</span>
                      <strong>{category} ({getCategoryCount(category)})</strong>
                    </div>
                    {openCategories[category] && (
                      <div style={{ paddingLeft: "1.5rem", marginTop: "0.5rem" }}>
                        {Object.keys(data[category]).map((subCategory) => (
                          <div key={subCategory} style={{ marginBottom: "0.5rem" }}>
                            <label style={{ display: "flex", alignItems: "center", gap: "0.5rem" }}>
                              <input
                                type="checkbox"
                                onChange={(e) => handleCheckboxChange(e.target.checked, category, subCategory)}
                                checked={selectedCheckboxes.some(item => 
                                  item.category === category && item.subCategory === subCategory
                                )}
                              />
                              {subCategory} ({getCategoryCount(category,subCategory)})
                            </label>
                          </div>
                        ))}
                      </div>
                    )}
                  </div>
                );
              }
              return null;
            })}
          </div>
        </Column>
            
        <Column lg={9} md={8} sm={6} style={{ 
          padding: "2rem",
          background: "white",
          borderRadius: "16px",
          boxShadow: "0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06)",
          maxHeight: "calc(100vh - 64px)",
          width: "70%",
          overflowY: "auto"
        }}>
          <div style={{ 
            fontWeight: "bold", 
            fontSize: "40px", 
            marginBottom: "2rem",
            color: "#2d3748",
            letterSpacing: "-0.025em" 
          }}>
            Artifacts Assessment Report
          </div>

          {fileType && fileList.length > 0 && (
            <div style={{ 
              fontWeight: "500", 
              marginBottom: "2rem",
              color: "#4a5568" 
            }}>
              
              {/* List of files */}
              {/* <div>
                {fileList.map((file, index) => (
                  <div key={index} style={{ marginBottom: "0.5rem", fontSize: "1rem" }}>
                    <li>{file}{fileType.toLowerCase() === 'zip' ? '' : `.${fileType.toLowerCase()}`}</li>
                  </div>
                ))}
              </div> */}
            </div>
          )}

          <div style={{ 
            fontWeight: "bold", 
            marginBottom: "2rem",
            color: "#4a5568",
	    fontSize: "1.2rem"
          }}>
            Total number of Applications assessed: {inputCount}
          </div>

          {/* {(inputFolder) && data?.["NodeJS"] && (
          <div style={{ fontWeight: "bold", marginBottom: "2rem", color: "#4a5568" }}>
            <p style={{ fontWeight: "bold", marginBottom: "0.75rem" }}>Applications of {inputFolder} are:</p>
            <ol style={{ fontWeight: "500", marginBottom: "1.5rem", color: "#4a5568", paddingLeft: "1.5rem", lineHeight: "1.6" }}>
              {data.NodeJS.map((app, index) => (
                <li key={index} style={{ marginBottom: "0.5rem" }}>{index + 1}. {app}</li>
              ))}
            </ol>
          </div>
        )} */}
{/* Heading */}
<div style={{ marginBottom: "1rem", fontSize: "1.1rem", fontWeight: "bold" }}>
                {fileType === 'Zip' ? 'Backup' : 'Bar'} {fileType.toLowerCase() === 'zip' ? (<> Files Analyzed: {inputFolder}.zip 
                <h6 style={{ marginTop: "0.5rem", fontSize: "1.1rem", fontWeight: "bold" }}>Integration Server(s) Analyzed:</h6></> ) : ('Files Analyzed :')}
              </div>

              <table style={{ 
  minWidth: "50%",
  borderCollapse: "separate",
  borderSpacing: "0",
  border: "1px solid #e2e8f0",
  borderRadius: "8px",
  overflow: "hidden",
  minHeight: "100px"
}}>
  <thead>
    <tr style={{ backgroundColor: "#f0f0f0" }}>
      <th style={{ 
        padding: "16px", 
        fontSize: "1.125rem", 
        textAlign: "left",
        color: "#2d3748",
        borderBottom: "2px solid #ccc", // Darker line
        borderRight: "1px solid #ccc",  // Darker line
        width: "20%" // Increased width for the first column
      }}>
        {listApp.length > 0 && listApp[0].file.endsWith(".bar") ? "Bar Files" : "Integration Server"}
      </th>
      <th style={{ 
        padding: "16px", 
        fontSize: "1.125rem", 
        textAlign: "left",
        color: "#2d3748",
        borderBottom: "2px solid #ccc",  // Darker line
        width: "80%" // Adjusting the width for the second column
      }}>
        Applications
      </th>
    </tr>
  </thead>
  <tbody>
    {listApp.length > 0 ? (
      listApp.map((item, index) => (
        <tr key={`${item.file}-${index}`} style={{
          backgroundColor: index % 2 === 0 ? "white" : "#f8fafc"
        }}>
          <td style={{ 
            padding: "12px 16px",
            color: "#4a5568",
            textAlign: "left",
            borderBottom: "1px solid #ccc", // Darker line
            borderRight: "1px solid #ccc"   // Darker line
          }}>
            {item.file}
          </td>
          <td style={{ 
            padding: "12px 16px",
            color: "#4a5568",
            textAlign: "left",
            borderBottom: "1px solid #ccc"  // Darker line
          }}>
            {item.applications}
          </td>
        </tr>
      ))
    ) : (
      <tr>
        <td colSpan={2} style={{ 
          padding: "16px",
          color: "#718096",
          textAlign: "center",
          backgroundColor: "white"
        }}>
          No applications found
        </td>
      </tr>
    )}
  </tbody>
</table>


          {renderAppComponentsTable()}
          
          {showLemTables && !selectedGroup && selectedCheckboxes.length === 0 && (
            <div style={{ 
              display: "flex", 
              flexDirection: "row", 
              gap: "1rem",
              overflowX: "auto",
              padding: "1rem",
              marginTop: "2rem"
            }}>
              {Object.entries(lemData).some(([_, data]) => Object.values(data).flat().length > 0) ? (
                <>
                  {renderLemTable('JavaNodes', lemData.JavaNodes)}
                  {renderLemTable('JavaScriptNodes', lemData.JavaScriptNodes)}
                  {renderLemTable('ResourceManagers', lemData.ResourceManagers)}
                </>
              ) : (
                <div style={{
                  padding: "2rem",
                  textAlign: "center",
                  color: "#4a5568",
                  fontSize: "1.125rem",
                  backgroundColor: "#f8fafc",
                  borderRadius: "8px",
                  border: "1px solid #e2e8f0",
                  width: "100%"
                }}>
                  No Java based resources found
                </div>
              )}
            </div>
          )}

          {selectedGroup && !showLemTables && selectedCheckboxes.length === 0 && (
            <div style={{ marginTop: "2rem" }}>
              <h2 style={{ 
                color: "#2d3748", 
                marginBottom: "1.5rem",
                fontSize: "1.5rem",
                fontWeight: "600"
              }}>{selectedGroup}</h2>
              {(() => {
                const categories = getGroupCategories(selectedGroup);
                
                
                // // Check if any category in this group has applications
                // const hasAnyApps = categories.some(category => {
                //   // For Database_ODBC, check if it exists in data
                //   if (category === "Database_ODBC") {
                //     return data["Database_ODBC"] && data["Database_ODBC"].length > 0;
                //   }
                //   return (data[category] || []).length > 0;
                // });
                const hasAnyApps = categories.some(category => {
                  const categoryName = category.includes("_") ? category.split("_").pop() : category;
                  return (data[categoryName] || []).length > 0;
              });

                if (!hasAnyApps) {
                  return (
                    <div style={{
                      padding: "2rem",
                      textAlign: "center",
                      color: "#4a5568",
                      fontSize: "1.125rem",
                      backgroundColor: "#f8fafc",
                      borderRadius: "8px",
                      border: "1px solid #e2e8f0"
                    }}>
                      No applications found for {selectedGroup}
                    </div>
                  );
                }

                return (
                  <div style={{ 
                    display: "flex", 
                    flexDirection: "row", 
                    flexWrap: "wrap",
                    gap: "2rem"
                  }}>
                    {categories.map((category) => {
                      // Get apps for this category
                      const apps = data[category] || [];
                      
                      if (apps.length === 0) return null;

                      return (
                        <TableWrapper key={category} id={category} title={category}>
                          <table style={{ 
                            width: "100%",
                            borderCollapse: "separate",
                            borderSpacing: "0",
                            marginBottom: "1rem",
                            border: "1px solid #e2e8f0",
                            borderRadius: "8px",
                            overflow: "hidden",
                            minHeight: "200px"
                          }}>
                            <thead>
                              <tr style={{ backgroundColor: "#f0f0f0" }}>
                                <th style={{ 
                                  padding: "16px", 
                                  fontSize: "1.125rem", 
                                  textAlign: "center",
                                  color: "#2d3748",
                                  borderBottom: "2px solid #e2e8f0"
                                }}>{category}</th>
                              </tr>
                            </thead>
                            <tbody>
                              {apps.map((app, index) => (
                                <tr key={`${app}-${index}`} style={{
                                  backgroundColor: index % 2 === 0 ? "white" : "#f8fafc"
                                }}>
                                  <td style={{ 
                                    padding: "12px 16px",
                                    color: "#4a5568",
                                    textAlign: "center",
                                    borderBottom: "1px solid #e2e8f0" 
                                  }}>{formatAppDisplay(app)}</td>
                                </tr>
                              ))}
                            </tbody>
                          </table>
                        </TableWrapper>
                      );
                    })}
                  </div>
                );
              })()}
            </div>
          )}

          {!selectedGroup && !showLemTables && selectedCheckboxes.length > 0 && (
            <div>
              <h2 style={{ 
                color: "#2d3748", 
                marginTop: "1.8rem",
                marginBottom: "1.5rem",
                fontSize: "1.5rem",
                fontWeight: "600"
              }}>Selected Categories Results</h2>
              <div style={{ marginBottom: "1rem" }}>
                <strong style={{ 
                  color: "#2d3748", 
                  display: "block", 
                  marginBottom: "0.5rem" 
                }}>Selected Categories:</strong>
                <ul style={{ 
                  listStyle: "none",
                  padding: "0",
                  margin: "0"
                }}>
                  {selectedCheckboxes.map(({ category, subCategory }, index) => (
                    <li key={index} style={{ 
                      color: "#4a5568",
                      padding: "4px 0"
                    }}>{category}{subCategory ? ` - ${subCategory}` : ''}</li>
                  ))}
                </ul>
              </div>

              {operationType === 'OR' ? (
                <div style={{ 
                  display: "flex", 
                  flexDirection: "row", 
                  flexWrap: "wrap",
                  gap: "2rem",
                  marginTop: "2rem"
                }}>
                  {selectedCheckboxes.length > 0 ? (
                    selectedCheckboxes.map(({ category, subCategory }) => {
                      const apps = subCategory ? data[category]?.[subCategory] : data[category];
                      const title = subCategory ? `${category} - ${subCategory}` : category;
                      
                      return (
                        <TableWrapper key={`${category}-${subCategory}`} id={`${category}-${subCategory}`} title={title}>
                          <table style={{ 
                            width: "100%",
                            borderCollapse: "separate",
                            borderSpacing: "0",
                            border: "1px solid #e2e8f0",
                            borderRadius: "8px",
                            overflow: "hidden",
                            minHeight: "200px"
                          }}>
                            <thead>
                              <tr style={{ backgroundColor: "#f0f0f0" }}>
                                <th style={{ 
                                  padding: "16px", 
                                  fontSize: "1.125rem", 
                                  textAlign: "center",
                                  color: "#2d3748",
                                  borderBottom: "2px solid #e2e8f0"
                                }}>{title}</th>
                              </tr>
                            </thead>
                            <tbody>
                              {apps && apps.length > 0 ? (
                                apps.map((app, index) => (
                                  <tr key={`${app}-${index}`} style={{
                                    backgroundColor: index % 2 === 0 ? "white" : "#f8fafc"
                                  }}>
                                    <td style={{ 
                                      padding: "12px 16px",
                                      color: "#4a5568",
                                      textAlign: "center",
                                      borderBottom: "1px solid #e2e8f0" 
                                    }}>{formatAppDisplay(app)}</td>
                                  </tr>
                                ))
                              ) : (
                                <tr>
                                  <td style={{ 
                                    padding: "16px",
                                    color: "#718096",
                                    textAlign: "center",
                                    backgroundColor: "white"
                                  }}>
                                    No applications found 
                                  </td>
                                </tr>
                              )}
                            </tbody>
                          </table>
                        </TableWrapper>
                      );
                    })
                  ) : (
                    <div style={{
                      padding: "2rem",
                      textAlign: "center",
                      color: "#4a5568",
                      fontSize: "1.125rem",
                      backgroundColor: "#f8fafc",
                      borderRadius: "8px",
                      border: "1px solid #e2e8f0",
                      width: "100%"
                    }}>
                      No categories selected
                    </div>
                  )}
                </div>
              ) : (
                <TableWrapper>
                <table style={{ 
                  width: "300px",
                  borderCollapse: "separate",
                  borderSpacing: "0",
                  border: "1px solid #e2e8f0",
                  borderRadius: "8px",
                  overflow: "hidden"
                }}>
                  <thead>
                    <tr style={{ backgroundColor: "#f0f0f0" }}>
                      <th style={{ 
                        padding: "16px",
                        fontSize: "1.125rem",
                        textAlign: "center",
                        color: "#2d3748",
                        borderBottom: "2px solid #e2e8f0"
                      }}>
                        Applications
                      </th>
                    </tr>
                  </thead>
                  <tbody>
                    {getCommonApps().map((app, index) => (
                      <tr key={`${app}-${index}`} style={{
                        backgroundColor: index % 2 === 0 ? "white" : "#f8fafc"
                      }}>
                        <td style={{ 
                          padding: "12px 16px",
                          color: "#4a5568",
                          textAlign: "center",
                          borderBottom: "1px solid #e2e8f0"
                        }}>{formatAppDisplay(app)}</td>
                      </tr>
                    ))}
                    {getCommonApps().length === 0 && (
                      <tr>
                        <td style={{ 
                          padding: "16px",
                          color: "#718096",
                          textAlign: "center",
                          backgroundColor: "white"
                        }}>
                          No applications found
                        </td>
                      </tr>
                    )}
                  </tbody>
                </table>
                </TableWrapper>
              )}
            </div>
          )}
        </Column>
      </Grid>
    </div>
  );
}

export default App;