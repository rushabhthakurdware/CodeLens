import React from 'react';
import * as Icons from 'lucide-react';
import { Link } from 'react-router-dom';

export default function ProjectCard({ project, onDelete }) {

const confirmDelete = (e) => {
    // Stop the click from triggering the "View Logic" navigation
    e.stopPropagation(); 
    e.preventDefault();

    if (window.confirm(`Permanently delete "${project.name}" and all AI analysis?`)) {
      onDelete(project.id);
    }
  };
  
  // Safety check: if project is null or undefined, don't crash
  const Github = Icons.Github || Icons.GithubIcon || Icons.Code;
  const Calendar = Icons.Calendar || Icons.Clock;
  const ArrowRight = Icons.ArrowRight || Icons.ChevronRight;
  const Activity = Icons.Activity || Icons.Zap;
    const Trash2 = Icons.Trash2 || Icons.XCircle || Icons.X;
  if (!project) return null;

  return (
    <div className="bg-[#1e293b]/50 border border-slate-800 p-6 rounded-2xl hover:border-blue-500/50 transition-all group shadow-xl">
      <button 
        onClick={confirmDelete}
        className="absolute top-4 right-4 p-2 text-slate-500 hover:text-red-500 hover:bg-red-500/10 rounded-lg opacity-0 group-hover:opacity-100 transition-all"
        title='delete project'
      >
        <Trash2 size={18} />
      </button>
      <div className="flex justify-between items-start mb-4">
        <div className="p-3 bg-slate-900 rounded-xl border border-slate-800">
          {/* Use a fallback if Github is undefined */}
          {Github ? <Github className="text-slate-400 group-hover:text-white" /> : <div className="w-6 h-6 bg-slate-700" />}
        </div>
        <div className="flex items-center gap-1.5 px-3 py-1 bg-green-500/10 border border-green-500/20 rounded-full">
          {Activity && <Activity size={12} className="text-green-500" />}
          <span className="text-[10px] font-bold text-green-500 uppercase tracking-widest">
            {project.description?.includes("Testing") ? "Draft" : "Analyzed"}
          </span>
        </div>
      </div>

      <h3 className="text-xl font-bold text-white mb-2">{project.name || "Unnamed Project"}</h3>
      <p className="text-slate-400 text-sm mb-6 line-clamp-2">
        {project.description || "No description available for this repository."}
      </p>

      <div className="flex items-center justify-between pt-4 border-t border-slate-800/50">
        <div className="flex items-center gap-2 text-slate-500 text-xs">
          {Calendar && <Calendar size={14} />}
          <span>ID: {project.id}</span>
        </div>

        <button className="flex items-center gap-2 text-blue-400 font-bold text-sm hover:text-blue-300 transition-colors">

          <Link to={`/projects/${project.id}`}
          className="flex items-center gap-2 text-blue-400 font-bold text-sm hover:text-blue-300">
            View Logic {ArrowRight && <ArrowRight size={16} />}
          </Link>
          
        </button>

      </div>
    </div>
  );
}