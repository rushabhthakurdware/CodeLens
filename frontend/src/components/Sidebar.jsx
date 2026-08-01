import { LayoutDashboard, PlusCircle, Settings, Code2, Cpu,Database } from 'lucide-react';
import { NavLink } from 'react-router-dom';
export default function Sidebar() {
  return (
    <aside className="h-screen w-64 bg-panel border-r border-slate-800 flex flex-col p-6 fixed left-0 top-0">
      <div className="flex items-center gap-3 mb-10 px-2">
        <div className="bg-accent/20 p-2 rounded-lg">
          <Cpu className="text-accent w-6 h-6" />
        </div>
        <span className="text-xl font-bold text-white tracking-tight">
          CodeLens <span className="text-accent">AI</span>
        </span>
      </div>
      
      {/* Navigation Options Frame */}
      <nav className="flex-1 space-y-2">
        <SidebarLink to="/" icon={<LayoutDashboard size={20}/>} label="Dashboard" end />
        <SidebarLink to="/new-project" icon={<PlusCircle size={20}/>} label="New Project" />
        <SidebarLink to="/explore" icon={<Code2 size={20}/>} label="Explore Logic" />
        <SidebarLink to="/analytics" icon={<Database size={20}/>} label="Analytics" />
      </nav>

      {/* Footer Support Option */}
      <div className="pt-4 border-t border-slate-800">
        <SidebarLink to="/settings" icon={<Settings size={20}/>} label="Settings" />
      </div>
    </aside>
  );
}

function SidebarLink({ to, icon, label, end = false }) {
  return (
    <NavLink
      to={to}
      end={end}
      className={({ isActive }) => `
        flex items-center gap-3 px-4 py-3 rounded-xl transition-all duration-200 font-semibold
        ${isActive 
          ? 'bg-accent/10 text-accent border border-accent/20' 
          : 'text-slate-400 hover:bg-slate-900 hover:text-white border border-transparent'
        }
      `}
    >
      {icon}
      <span>{label}</span>
    </NavLink>
  );
}