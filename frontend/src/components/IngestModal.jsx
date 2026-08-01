import React, { useState } from 'react';
import { X, Link2, Terminal, Loader2, Sparkles } from 'lucide-react';
import { projectService } from '../services/api';

export default function IngestModal({ isOpen, onClose, onRefresh }) {
  const [formData, setFormData] = useState({ name: '', url: '', description: '' });
  const [isAnalyzing, setIsAnalyzing] = useState(false);

  const [selectedExts, setSelectedExts] = useState(['java', 'py', 'js', 'jsx']); // Default logic files
  const commonExts = ['java', 'py', 'js', 'jsx', 'ts', 'tsx', 'cpp', 'html', 'css', 'go', 'rs'];
  const toggleExtension = (ext) => {
  setSelectedExts(prev => 
    prev.includes(ext) ? prev.filter(e => e !== ext) : [...prev, ext]
  );
};
  if (!isOpen) return null;

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsAnalyzing(true);
    try {
      await projectService.addProject(formData);
      onRefresh(); // Refresh the dashboard list
      onClose();   // Close modal
      setFormData({ name: '', url: '', description: '' });
    } catch (error) {
      alert("Ingestion failed. Ensure the URL is public and the AI service is running.");
    } finally {
      setIsAnalyzing(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm">
      <div className="bg-[#0f172a] border border-slate-800 w-full max-w-lg rounded-3xl overflow-hidden shadow-2xl">
        
        {/* Header */}
        <div className="flex justify-between items-center p-6 border-b border-slate-800 bg-slate-900/50">
          <div className="flex items-center gap-2">
            <div className="bg-blue-500/20 p-2 rounded-lg text-blue-500">
              <Sparkles size={20} />
            </div>
            <h2 className="text-xl font-bold text-white">Ingest Repository</h2>
          </div>
          <button onClick={onClose} className="text-slate-500 hover:text-white"><X /></button>
        </div>

        {isAnalyzing ? (
          <div className="p-20 flex flex-col items-center justify-center text-center">
            <Loader2 className="animate-spin text-blue-500 mb-6" size={48} />
            <h3 className="text-xl font-bold text-white mb-2">Analyzing Logic...</h3>
            <p className="text-slate-400">CodeLens is cloning the repo and generating AI summaries. This may take a minute.</p>
          </div>
        ) : (
          <form onSubmit={handleSubmit} className="p-8 space-y-6">
            <div>
              <label className="block text-xs font-bold text-slate-500 uppercase tracking-widest mb-2">Project Name</label>
              <input 
                required
                className="w-full bg-slate-950 border border-slate-800 rounded-xl px-4 py-3 text-white focus:border-blue-500 outline-none transition-all"
                placeholder="e.g. My Awesome Backend"
                value={formData.name}
                onChange={(e) => setFormData({...formData, name: e.target.value})}
              />
            </div>

            <div>
              <label className="block text-xs font-bold text-slate-500 uppercase tracking-widest mb-2">GitHub URL</label>
              <div className="relative">
                <Link2 className="absolute left-4 top-3.5 text-slate-600" size={18} />
                <input 
                  required
                  className="w-full bg-slate-950 border border-slate-800 rounded-xl pl-12 pr-4 py-3 text-white focus:border-blue-500 outline-none transition-all"
                  placeholder="https://github.com/user/repo"
                  value={formData.url}
                  onChange={(e) => setFormData({...formData, url: e.target.value})}
                />
              </div>
            </div>

            <div>
  <label className="block text-xs font-bold text-slate-500 uppercase mb-3">Select Files to Analyze</label>
  <div className="flex flex-wrap gap-2">
    {commonExts.map(ext => (
      <button
        key={ext}
        type="button"
        onClick={() => toggleExtension(ext)}
        className={`px-3 py-1 rounded-full text-xs font-bold border transition-all ${
          selectedExts.includes(ext) 
          ? 'bg-blue-600 border-blue-500 text-white' 
          : 'bg-slate-900 border-slate-800 text-slate-500 hover:border-slate-600'
        }`}
      >
        .{ext}
      </button>
    ))}
  </div>
</div>

            <div>
              <label className="block text-xs font-bold text-slate-500 uppercase tracking-widest mb-2">Short Description</label>
              <textarea 
                className="w-full bg-slate-950 border border-slate-800 rounded-xl px-4 py-3 text-white focus:border-blue-500 outline-none transition-all h-24"
                placeholder="What does this code do?"
                value={formData.description}
                onChange={(e) => setFormData({...formData, description: e.target.value})}
              />
            </div>

            <button type="submit" className="w-full bg-blue-600 hover:bg-blue-500 text-white font-bold py-4 rounded-2xl transition-all shadow-lg shadow-blue-600/20 active:scale-[0.98]">
              Begin Analysis
            </button>
          </form>
        )}
      </div>
    </div>
  );
}