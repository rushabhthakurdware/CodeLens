import { useState, useEffect } from 'react';
import ProjectCard from '../components/ProjectCard'; // Note the ../ to go up a folder
import { projectService } from '../services/api';
import { Loader2, Plus } from 'lucide-react';
import IngestModal from '../components/IngestModal'; // Import the modal

export default function Dashboard() {
  const [projects, setProjects] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false); // Modal State


  // Define this outside or use useCallback if you want to be extra fancy
const fetchProjects = async () => {
  setLoading(true); // Added this to show loader during manual refreshes
  try {
    const data = await projectService.getAllProjects();
    setProjects(Array.isArray(data) ? data : []);
  } catch (error) {
    console.error("Backend connection failed:", error);
  } finally {
    setLoading(false);
  }
};
  useEffect(() => {
  fetchProjects();
}, []);

  const handleDeleteProject = async (id) => {
  try {
    await projectService.deleteProject(id);
    // Filter out the deleted project from the state
    setProjects(prev => prev.filter(p => p.id !== id));
  } catch (error) {
    alert("Failed to delete project. Check backend logs.");
  }
};

  return (
    <div className="p-10">
      <header className="flex justify-between items-end mb-12">
        <div>
          <h1 className="text-4xl font-black text-white tracking-tight">
            Intelligence <span className="text-blue-500">Dashboard</span>
          </h1>
          <p className="text-slate-400 mt-2 text-lg">
            Manage your AI-analyzed repositories.
          </p>
        </div>
        
        <button 
          onClick={() => setIsModalOpen(true)}
          className="bg-blue-600 hover:bg-blue-500 text-white px-6 py-3 rounded-2xl font-bold flex items-center gap-2 transition-all"
        >
          <Plus size={20} /> Ingest Repository
        </button>
      </header>
{/* ADD THE MODAL COMPONENT */}
      <IngestModal 
        isOpen={isModalOpen} 
        onClose={() => setIsModalOpen(false)} 
        onRefresh={fetchProjects} 
      />
      {loading ? (
        <div className="flex flex-col items-center justify-center h-64">
          <Loader2 className="animate-spin text-blue-500 w-10 h-10 mb-4" />
          <p className="text-slate-500 animate-pulse font-medium">Syncing with CodeLens Engine...</p>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-8">
          {projects.map((project) => (
            <ProjectCard key={project.id}
             project={project}
             onDelete={handleDeleteProject} />
          ))}

          {projects.length === 0 && (
            <div className="col-span-full border-2 border-dashed border-slate-800 rounded-3xl p-20 text-center bg-slate-900/20">
              <p className="text-slate-600 text-xl font-medium">
                No repositories found. Start your first analysis!
              </p>
            </div>
          )}
        </div>
      )}
    </div>
  );
}