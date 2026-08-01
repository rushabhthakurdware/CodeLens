import { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { projectService } from '../services/api';
import { FileCode, ChevronLeft, Terminal, Info } from 'lucide-react';

export default function ProjectExplorer() {
    
  const { id } = useParams();
  const [files, setFiles] = useState([]);
  const [selectedFile, setSelectedFile] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const loadFiles = async () => {
      try {
        const data = await projectService.getProjectFiles(id);
        setFiles(data);
        if (data.length > 0) setSelectedFile(data[0]); // Default to first file
      } catch (err) {
        console.error("Failed to load files", err);
      } finally {
        setLoading(false);
      }
    };
    loadFiles();
  }, [id]);

  if (loading) return <div className="p-10">Analyzing File Tree...</div>;

  return (
    <div className="h-screen flex flex-col">
      {/* Top Header */}
      <div className="h-16 border-b border-slate-800 flex items-center px-6 justify-between bg-slate-950/50 backdrop-blur-md">
        <Link to="/" className="flex items-center gap-2 text-slate-400 hover:text-white transition-colors">
          <ChevronLeft size={20} /> Back to Dashboard
        </Link>
        <div className="flex items-center gap-2 text-blue-400 font-mono text-sm">
          <Terminal size={16} /> project_id_{id}
        </div>
      </div>

      <div className="flex-1 flex overflow-hidden">
        {/* Left Sidebar: File List */}
        <div className="w-80 border-r border-slate-800 bg-slate-900/20 overflow-y-auto p-4">
          <h2 className="text-xs font-bold text-slate-500 uppercase tracking-widest mb-4 px-2">Interesting Files</h2>

          <div className="space-y-1">
            {files.length > 0 ?(
              files.map(file => (
                <div 
                  key={file.id}
                  onClick={() => setSelectedFile(file)}
                  className={`flex items-center gap-3 px-3 py-2.5 rounded-lg cursor-pointer transition-all ${selectedFile?.id === file.id ?
                            'bg-blue-600/20 text-blue-400 border border-blue-500/30' : 'hover:bg-slate-800 text-slate-400'}`}
                >
                  <FileCode size={18} />
                  <span className="text-sm font-medium truncate">{file.fileName}</span>
                </div>
                )
              )
            ):(
              <div className="text-slate-500 text-sm italic px-2 py-10 text-center">
                <p className="text-slate-600 text-sm">No analyzed files found for this project.</p>
                <button className="mt-4 text-xs text-blue-500 hover:underline">Re-run Analysis</button>
              </div>
            )
          }


          </div>
        </div>

        {/* Right Panel: AI Summary */}
        <div className="flex-1 bg-[#020617] p-8 overflow-y-auto">
          {selectedFile ? (
            <div className="max-w-3xl mx-auto">
              <div className="flex items-center gap-3 mb-6">
                <div className="p-3 bg-blue-500/10 rounded-2xl text-blue-500">
                  <Info size={24} />
                </div>
                <div>
                  <h1 className="text-2xl font-bold text-white">{selectedFile.fileName}</h1>
                  <p className="text-slate-500 text-sm font-mono">{selectedFile.filePath}</p>
                </div>
              </div>

              <div className="bg-slate-900/50 border border-slate-800 rounded-3xl p-8 shadow-2xl relative overflow-hidden">
                <div className="absolute top-0 left-0 w-1 h-full bg-blue-500" />
                <h3 className="text-blue-400 font-bold mb-4 uppercase text-xs tracking-widest">AI Intelligence Summary</h3>
                <p className="text-slate-300 leading-relaxed text-lg italic font-serif">
                  "{selectedFile.summary}"
                </p>
              </div>
            </div>
          ) : (
            <div className="h-full flex items-center justify-center text-slate-600">
              Select a file to view AI logic insights
            </div>
          )}
        </div>
      </div>
    </div>
  );
}