import { useState, useEffect } from 'react';
import Sidebar from './components/Sidebar';
import ProjectCard from './components/ProjectCard';
import { projectService } from './services/api';
import { Loader2, Plus } from 'lucide-react';
import { Routes } from 'react-router-dom';
import { Route } from 'react-router-dom';
import Dashboard from './pages/Dashboard';
import ProjectExplorer from './pages/ProjectExplorer';  
import AnalyticsPage from './pages/AnalyticsPage';
function App() {

 
  return (
    <div className="flex bg-[#020617] min-h-screen text-slate-200">
      <Sidebar />
      <div className="flex-1 ml-64">
        <Routes>
          <Route path="/" element={<Dashboard />} />
          <Route path="/projects/:id" element={<ProjectExplorer />} />
          <Route path="/analytics" element={<AnalyticsPage />} />
        </Routes>
      </div>
    </div>
  )

}

export default App;