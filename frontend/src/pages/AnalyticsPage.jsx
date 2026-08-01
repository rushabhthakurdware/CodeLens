import React, { useState, useEffect } from 'react';
import TokenTracker from '../components/TokenTracker';
import { Loader2, RefreshCw, Database } from 'lucide-react';

export default function AnalyticsPage() {
  const [stats, setStats] = useState({ tokensConsumed: 0, tokensSaved: 0 });
  const [loading, setLoading] = useState(true);

  const fetchTokenData = async () => {
    setLoading(true);
    try {
      const response = await fetch("http://localhost:8081/api/projects/analytics/tokens");
      if (!response.ok) throw new Error("Failed to fetch statistics");
      const data = await response.json();
      setStats(data);
    } catch (error) {
      console.error("Error loading metrics:", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchTokenData();
  }, []);

  return (
    <div className="p-10 min-h-screen text-white bg-[#020617]">
      <div className="flex justify-between items-start mb-12">
        <div>
          <h1 className="text-4xl font-black tracking-tight">
            System <span className="text-blue-500">Analytics</span>
          </h1>
          <p className="text-slate-400 mt-2 text-lg">
            Monitor infrastructure costs, API performance bounds, and token caching metrics.
          </p>
        </div>
        
        <button 
          onClick={fetchTokenData}
          disabled={loading}
          className="p-3 bg-slate-900 border border-slate-800 hover:border-slate-600 rounded-xl transition-all text-slate-400 hover:text-white disabled:opacity-40"
        >
          <RefreshCw size={18} className={loading ? 'animate-spin' : ''} />
        </button>
      </div>

      {loading ? (
        <div className="flex flex-col items-center justify-center h-64">
          <Loader2 className="animate-spin text-blue-500 w-10 h-10 mb-4" />
          <p className="text-slate-500 font-medium">Extracting performance logs from PostgreSQL...</p>
        </div>
      ) : (
        <div className="space-y-8">
          <TokenTracker stats={stats} />

          <div className="bg-[#1e293b]/20 border border-slate-800/80 rounded-2xl p-8">
            <h3 className="text-lg font-bold flex items-center gap-2 mb-4 text-slate-300">
              <Database size={18} className="text-blue-500" /> 
              Infrastructure Efficiency Breakdown
            </h3>
            <div className="space-y-4 text-sm text-slate-400 max-w-3xl leading-relaxed">
              <p>
                By shifting core resource tracking to use the <span className="text-white font-semibold">GitHub REST Tree API</span>, local workspace storage usage has dropped to zero.
              </p>
              <p>
                The integrated <span className="text-green-400 font-semibold">SHA-256 Content-Hash matching layer</span> automatically prevents redundant multi-token operations on duplicate or unchanged source files, serving matching summaries in under <span className="text-white font-semibold">10ms</span> directly from your historical database cache.
              </p>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}