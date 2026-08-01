import React from 'react';
import { Zap, ShieldCheck, Coins } from 'lucide-react';

export default function TokenTracker({ stats }) {
  // Dollar baseline calculation: ~$0.000002 per token
  const dollarsSaved = (stats.tokensSaved * 0.000002).toFixed(4);

  return (
    <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-12">
      
      {/* Card 1: Tokens Burned */}
      <div className="bg-[#1e293b]/40 border border-slate-800 p-6 rounded-2xl flex items-center gap-4">
        <div className="p-4 bg-amber-500/10 rounded-xl border border-amber-500/20 text-amber-500">
          <Zap size={24} />
        </div>
        <div>
          <p className="text-xs font-bold text-slate-500 uppercase tracking-widest">Tokens Consumed</p>
          <h3 className="text-2xl font-black text-white mt-1">{stats.tokensConsumed.toLocaleString()}</h3>
        </div>
      </div>

      {/* Card 2: Tokens Saved */}
      <div className="bg-[#1e293b]/40 border border-slate-800/80 p-6 rounded-2xl flex items-center gap-4 hover:border-green-500/30 transition-all">
        <div className="p-4 bg-green-500/10 rounded-xl border border-green-500/20 text-green-500">
          <ShieldCheck size={24} />
        </div>
        <div>
          <p className="text-xs font-bold text-green-500 uppercase tracking-widest flex items-center gap-1">
            Tokens Saved <span className="text-[10px] bg-green-500/20 px-1.5 py-0.5 rounded text-green-400">Cache</span>
          </p>
          <h3 className="text-2xl font-black text-white mt-1">+{stats.tokensSaved.toLocaleString()}</h3>
        </div>
      </div>

      {/* Card 3: Expenses Saved */}
      <div className="bg-[#1e293b]/40 border border-slate-800 p-6 rounded-2xl flex items-center gap-4">
        <div className="p-4 bg-blue-500/10 rounded-xl border border-blue-500/20 text-blue-400">
          <Coins size={24} />
        </div>
        <div>
          <p className="text-xs font-bold text-slate-500 uppercase tracking-widest">API Expenses Saved</p>
          <h3 className="text-2xl font-black text-blue-400 mt-1">${dollarsSaved} <span className="text-xs text-slate-500 font-normal">USD</span></h3>
        </div>
      </div>

    </div>
  );
}